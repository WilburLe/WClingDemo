package com.wilbur.clingdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.wilbur.clingdemo.Utils.Utils;
import com.wilbur.clingdemo.dms.MediaServer;
import com.wilbur.clingdemo.manager.ManagerDLNA;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.VideoItem;
import org.seamless.util.MimeType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wilbur on 2018/1/19.
 */

public class BrowserActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SeekBar.OnSeekBarChangeListener {
    private Context mContext;
    private ListView mDeviceList;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mTVSelected;
    private SeekBar mSeekProgress;
    private SeekBar mSeekVolume;
    private Switch mSwitchMute;
    private ArrayAdapter<Device> mDevicesAdapter;
    private ManagerDLNA managerDLNA;
    private ControlPoint mControlPoint;
    private static String TAG = "BrowserActivity";
    private static int curState = 0;
    /** 播放状态 */
    public static final int PLAY = 1;
    /** 暂停状态 */
    public static final int PAUSE = 2;
    /** 停止状态 */
    public static final int STOP = 3;
    /** 转菊花状态 */
    public static final int BUFFER = 4;
    /** 投放失败 */
    public static final int ERROR = 5;
    private static final String DIDL_LITE_FOOTER = "</DIDL-Lite>";
    private static final String DIDL_LITE_HEADER = "<?xml version=\"1.0\"?>" +
            "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
            "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";
    private String mTestUrl = "http://video19.ifeng.com/video07/2013/11/11/281708-102-007-1138.mp4";
    private String mIP = "";
    public static final int IMAGE_TYPE = 0;
    public static final int VIDEO_TYPE = 1;
    public static final int AUDIO_TYPE = 2;
    public int curItemType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        String address = Utils.getIPAddress(true);
        mIP = "http://" + address + ":" + MediaServer.PORT;


        initView();
        bindUpnpServices();
        initListeners();

//        selectVideo();

//        selectAudio();

        selectImage();
    }

    private void bindUpnpServices() {
        managerDLNA = new ManagerDLNA(this,new BrowseRegistryListener());
        managerDLNA.initConnection();
        // Bind UPnP service
        boolean type = getApplicationContext().bindService(new Intent(this, AndroidUpnpServiceImpl.class),
                managerDLNA.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        mDeviceList = (ListView) findViewById(R.id.lv_devices);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        mTVSelected = (TextView) findViewById(R.id.tv_selected);
        mSeekProgress = (SeekBar) findViewById(R.id.seekbar_progress);
        mSeekVolume = (SeekBar) findViewById(R.id.seekbar_volume);
        mSwitchMute = (Switch) findViewById(R.id.sw_mute);

        mDevicesAdapter = new DevicesAdapter(mContext);
        mDeviceList.setAdapter(mDevicesAdapter);

        /** 这里为了模拟 seek 效果(假设视频时间为 15s)，拖住 seekbar 同步视频时间，
         * 在实际中 使用的是片源的时间 */
        mSeekProgress.setMax(15);

        // 最大音量就是 100，不要问我为什么
        mSeekVolume.setMax(100);
    }

    private void initListeners() {

        mRefreshLayout.setOnRefreshListener(this);

        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 选择连接设备
                Device item = mDevicesAdapter.getItem(position);
                if (Utils.isNull(item)) {
                    return;
                }

                Device device = item;
                if (Utils.isNull(device)) {
                    return;
                }
                mDevice = device;
                String selectedDeviceName = String.format(getString(R.string.selectedText), device.getDetails().getFriendlyName());
                mTVSelected.setText(selectedDeviceName);
            }
        });
    }

    public class BrowseRegistryListener extends DefaultRegistryListener {
        @Override
        public void deviceAdded(Registry registry, final Device device) {
            super.deviceAdded(registry, device);
            runOnUiThread(new Runnable() {
                public void run() {
                    mDevicesAdapter.add(device);
                }
            });
        }

        @Override
        public void deviceRemoved(Registry registry, final Device device) {
            super.deviceRemoved(registry, device);
            runOnUiThread(new Runnable() {
                public void run() {
                    mDevicesAdapter.remove(device);
                }
            });
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.bt_play:
                play();
                break;

            case R.id.bt_pause:
//                pause();
                break;

            case R.id.bt_stop:
//                stop();
                break;
        }
    }

    public static final ServiceType AV_TRANSPORT_SERVICE = new UDAServiceType("AVTransport");
    private Device mDevice;
    private void play() {
        if(curState == 0 || curState == STOP){
            Log.e(TAG,mTestUrl);
            setAVTransportURI(mTestUrl,curItemType);
        }else if(curState == PAUSE){
            final Service avtService = mDevice.findService(AV_TRANSPORT_SERVICE);
            if (Utils.isNull(avtService)) {
                return;
            }

            if (Utils.isNull(mControlPoint)) {
                return;
            }

            mControlPoint.execute(new Play(avtService) {
                @Override
                public void success(ActionInvocation invocation) {
                    super.success(invocation);
                    curState = PLAY;
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    curState = ERROR;
                }
            });
        }
    }

    /**
     * 设置片源，用于首次播放
     *
     * @param url   片源地址
     */
    private void setAVTransportURI(String url,int itemType) {
        if (Utils.isNull(url)) {
            return;
        }

        String metadata = pushMediaToRender(url, "id", "name", "0",itemType);

        final Service avtService = mDevice.findService(AV_TRANSPORT_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }

        mControlPoint = managerDLNA.getControlPoint();
        if (Utils.isNull(mControlPoint)) {
            return;
        }

        mControlPoint.execute(new SetAVTransportURI(avtService, url,metadata) {
            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                curState = PLAY;
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Log.e(TAG,"play error:"+defaultMsg);
                curState = ERROR;
            }
        });
    }

    private String pushMediaToRender(String url, String id, String name, String duration,int ItemType) {
        long size = 0;
        long bitrate = 0;
        Res res = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), size, url);

        String creator = "unknow";
        String resolution = "unknow";
        String metadata = null;

        switch (ItemType){
            case IMAGE_TYPE:
                ImageItem imageItem = new ImageItem(id, "0", name, creator, res);
                metadata = createItemMetadata(imageItem);
                break;
            case VIDEO_TYPE:
                VideoItem videoItem = new VideoItem(id, "0", name, creator, res);
                metadata = createItemMetadata(videoItem);
                break;
            case AUDIO_TYPE:
                AudioItem audioItem = new AudioItem(id,"0",name,creator,res);
                metadata = createItemMetadata(audioItem);
                break;
        }

        Log.e(TAG, "metadata: " + metadata);
        return metadata;
    }

    private String createItemMetadata(DIDLObject item) {
        StringBuilder metadata = new StringBuilder();
        metadata.append(DIDL_LITE_HEADER);

        metadata.append(String.format("<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">", item.getId(), item.getParentID(), item.isRestricted() ? "1" : "0"));

        metadata.append(String.format("<dc:title>%s</dc:title>", item.getTitle()));
        String creator = item.getCreator();
        if (creator != null) {
            creator = creator.replaceAll("<", "_");
            creator = creator.replaceAll(">", "_");
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator));
        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.getClazz().getValue()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        metadata.append(String.format("<dc:date>%s</dc:date>", time));

        Res res = item.getFirstResource();
        if (res != null) {
            // protocol info
            String protocolinfo = "";
            ProtocolInfo pi = res.getProtocolInfo();
            if (pi != null) {
                protocolinfo = String.format("protocolInfo=\"%s:%s:%s:%s\"", pi.getProtocol(), pi.getNetwork(), pi.getContentFormatMimeType(), pi
                        .getAdditionalInfo());
            }
            Log.e(TAG, "protocolinfo: " + protocolinfo);

            // resolution, extra info, not adding yet
            String resolution = "";
            if (res.getResolution() != null && res.getResolution().length() > 0) {
                resolution = String.format("resolution=\"%s\"", res.getResolution());
            }

            // duration
            String duration = "";
            if (res.getDuration() != null && res.getDuration().length() > 0) {
                duration = String.format("duration=\"%s\"", res.getDuration());
            }

            // res begin
            //            metadata.append(String.format("<res %s>", protocolinfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolinfo, resolution, duration));

            // url
            String url = res.getValue();
            metadata.append(url);

            // res end
            metadata.append("</res>");
        }
        metadata.append("</item>");

        metadata.append(DIDL_LITE_FOOTER);

        return metadata.toString();
    }

    private void selectVideo() {
        curItemType = VIDEO_TYPE;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 222);
    }

    private void selectAudio() {
        curItemType = AUDIO_TYPE;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 222);
    }

    private void selectImage() {
        curItemType = IMAGE_TYPE;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 222);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            Uri uri = data.getData();
            String path = Utils.getRealPathFromUriAboveApi19(mContext, uri);
            mTestUrl = mIP + path;
            Log.e(TAG, path);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

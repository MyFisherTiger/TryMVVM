package com.kjs.readpagerlibrary.readpage;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mengmengda.reader.R;
import com.mengmengda.reader.activity.BookReadActivity;
import com.mengmengda.reader.adapter.TTSVoiceNameRecycleAdapter;
import com.mengmengda.reader.been.C;
import com.mengmengda.reader.common.SharePreferenceUtils;
import com.mengmengda.reader.readpage.baidutts.OfflineResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 阅读页听书弹出框
 * Created by yangyan
 * on 2018/2/12.
 */

public class ReadSpeechDialog extends PopupWindow implements BaseQuickAdapter.OnRecyclerViewItemClickListener,SpeechSynthesizerListener {

	@BindView(R.id.read_speech_ll_menu)
    RelativeLayout read_speech_ll_menu;
	@BindView(R.id.recyclerView_TTSVoiceName)
    RecyclerView recyclerView_TTSVoiceName;
	@BindView(R.id.seekBar_TTSSpeed)
    SeekBar seekBar_TTSSpeed;

	private PageLoader mPageLoader;
	private View mainView;
	private Activity activity;
	private int turnPageCount=0;
	private int addSpeechSynthesizeBagCount=0;
	// 加载发音人数据
	private String[] ttsVoiceLabels;
	private String[] ttsVoiceValues;
	private TTSVoiceNameRecycleAdapter ttsVoiceNameRecycleAdapter;
	private String AppId = "9547046";
	private String AppKey = "wWPA5tmqzfUuP4ARHQcXKktH";
	private String AppSecret = "xirt4mEEGGFOKzXsnHMGAcsK9mlmBHSa";
	private SpeechSynthesizer mSpeechSynthesizer;
	private int pos;
	// 离线发音选择
	private String offlineVoice = OfflineResource.VOICE_MALE;
	private String speedStr;
	private String voiceName;
	private int speed;

	public ReadSpeechDialog(Activity activity, PageLoader mPageLoader) {
		this.activity = activity;
		this.mPageLoader = mPageLoader;
		mainView = LayoutInflater.from(activity).inflate(R.layout.dialog_read_speech, null);
		setContentView(mainView);
		ButterKnife.bind(this, mainView);
		//设置宽度
		setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
		//设置高度
		setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		//设置背景透明
		setBackgroundDrawable(null);
		mainView.setFocusable(true);
		mainView.setFocusableInTouchMode(true);

		read_speech_ll_menu.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dismiss();
				return false;
			}
		});

		setAnimationStyle(R.style.anim_slide_bottom_in_out);
		initData();
	}

	private void initData() {

		// 语音朗读
		speedStr = SharePreferenceUtils.getPrefByPackage(activity, SharePreferenceUtils.SP_KEY_TTS_SPEED_STR, C.TTS_DEFAULT_SPEED + "");
		voiceName = SharePreferenceUtils.getPrefByPackage(activity, SharePreferenceUtils.SP_KEY_TTS_VOICE_NAME_STR, activity.getString(R.string.ttsDefaultVoiceName));
		ttsVoiceLabels = activity.getResources().getStringArray(R.array.tts_voice_cloud_entries);
		ttsVoiceValues = activity.getResources().getStringArray(R.array.tts_voice_cloud_values);

		speed = Integer.parseInt(speedStr);
		seekBar_TTSSpeed.setMax(9);
		seekBar_TTSSpeed.setProgress(speed);
		seekBar_TTSSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				SharePreferenceUtils.setPrefByPackage(activity, SharePreferenceUtils.SP_KEY_TTS_SPEED_STR, progress + "");
				reNewSpeak();
			}
		});

		int selectPosition = -1;
		for (int i = 0; i < ttsVoiceValues.length; i++) {
			if (ttsVoiceValues[i].equals(voiceName)) {
				selectPosition = i;
			}
		}
		recyclerView_TTSVoiceName.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
		ttsVoiceNameRecycleAdapter = new TTSVoiceNameRecycleAdapter(Arrays.asList(ttsVoiceLabels));
		ttsVoiceNameRecycleAdapter.setOnRecyclerViewItemClickListener(this);
		ttsVoiceNameRecycleAdapter.setSelectPosition(selectPosition);
		recyclerView_TTSVoiceName.setAdapter(ttsVoiceNameRecycleAdapter);
	}


	public void show(View view) {
		showAsDropDown(view);
		update();
	}

	@Override
	public void onItemClick(View view, int i) {
		ttsVoiceNameRecycleAdapter.setSelectPosition(i);
		ttsVoiceNameRecycleAdapter.notifyDataSetChanged();
		SharePreferenceUtils.setPrefByPackage(activity, SharePreferenceUtils.SP_KEY_TTS_VOICE_NAME_STR, ttsVoiceValues[i]);
		reNewSpeak();
	}

	public void reNewSpeak(){
		if(mSpeechSynthesizer!=null){
			mSpeechSynthesizer.stop();
			setTTsParams();
			speak(pos);
		}
	}


	/**
	 * 语音合成播放
	 */
	public void speak(int pos) {
		this.pos=pos;
		addSpeechSynthesizeBagCount=0;
		turnPageCount=0;
		if (mSpeechSynthesizer != null) {
			List<TxtPage> txtPages=mPageLoader.loadPageList(mPageLoader.getChapterPos());
			List<SpeechSynthesizeBag> bags = new ArrayList<>();
			StringBuilder stringBuilder=new StringBuilder("");
			for(int i=0;i<txtPages.get(pos).lines.size();i++){
				if(txtPages.get(pos).lines.get(i).contains("\n")||i==txtPages.get(pos).lines.size()-1){
					stringBuilder.append(txtPages.get(pos).lines.get(i));
					bags.add(getSpeechSynthesizeBag(stringBuilder.toString(),i+""));
					addSpeechSynthesizeBagCount++;
					stringBuilder=new StringBuilder("");
				}else{
					stringBuilder.append(txtPages.get(pos).lines.get(i));
				}

			}
			mSpeechSynthesizer.batchSpeak(bags);
		}
	}

	private SpeechSynthesizeBag getSpeechSynthesizeBag(String text, String utteranceId) {
		SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
		//需要合成的文本text的长度不能超过1024个GBK字节。
		speechSynthesizeBag.setText(text);
		speechSynthesizeBag.setUtteranceId(utteranceId);
		return speechSynthesizeBag;
	}

	/**
	 * 百度听书初始化
	 */
	public void initialTts() {
		// 日志打印在logcat中
		LoggerProxy.printable(true);
		//百度听书初始化
		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		mSpeechSynthesizer.setContext(activity);
		mSpeechSynthesizer.setSpeechSynthesizerListener(this);
		mSpeechSynthesizer.setAppId(AppId);
		mSpeechSynthesizer.setApiKey(AppKey, AppSecret);
		setTTsParams();
	}

	private void setTTsParams(){

		speedStr = SharePreferenceUtils.getPrefByPackage(activity, SharePreferenceUtils.SP_KEY_TTS_SPEED_STR, C.TTS_DEFAULT_SPEED + "");
		voiceName = SharePreferenceUtils.getPrefByPackage(activity, SharePreferenceUtils.SP_KEY_TTS_VOICE_NAME_STR, activity.getString(R.string.ttsDefaultVoiceName));
		speed = Integer.parseInt(speedStr);


		for(int i=0;i<ttsVoiceValues.length;i++){
			if(voiceName.equals("0")){
				offlineVoice=OfflineResource.VOICE_FEMALE;
			}else if(voiceName.equals("1")){
				offlineVoice=OfflineResource.VOICE_MALE;
			}else if(voiceName.equals("3")){
				offlineVoice=OfflineResource.VOICE_DUXY;
			}else if(voiceName.equals("4")){
				offlineVoice=OfflineResource.VOICE_DUYY;
			}
		}


		// 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, voiceName);
		// 设置合成的音量，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, speedStr);
		// 设置合成的语速，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, speed+"");
		// 设置合成的语调，0-9 ，默认 5
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "7");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
		// 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
		// MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
		// MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

		// 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
		OfflineResource offlineResource = createOfflineResource(offlineVoice);
		// 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
				offlineResource.getModelFilename());

		//离在线混合
		mSpeechSynthesizer.initTts(TtsMode.MIX);
	}

	private OfflineResource createOfflineResource(String voiceType) {
		OfflineResource offlineResource = null;
		try {
			offlineResource = new OfflineResource(activity, voiceType);
		} catch (IOException e) {
			// IO 错误自行处理
			e.printStackTrace();
		}
		return offlineResource;
	}

	@OnClick({R.id.tv_TTSExit})
	public void onMenuClick(View view){
		switch (view.getId()){
			case R.id.tv_TTSExit:
				activity.onBackPressed();
				break;
		}
	}

	/**
	 * 暂停播放。仅调用speak后生效
	 */
	public void pause() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.pause();
		}
	}

	/**
	 * 继续播放。仅调用speak后生效，调用pause生效
	 */
	public void resume() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.resume();
		}
	}

	public void stop() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.stop();
		}
	}

	public void onDestroy() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.release();
		}
	}

	//回调监听
	@Override
	public void onSynthesizeStart(String s) {

	}

	@Override
	public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

	}

	@Override
	public void onSynthesizeFinish(String s) {

	}

	@Override
	public void onSpeechStart(String s) {

	}

	@Override
	public void onSpeechProgressChanged(String s, int i) {

	}

	@Override
	public void onSpeechFinish(String s) {
		turnPageCount++;
		if(turnPageCount==addSpeechSynthesizeBagCount){
			((BookReadActivity)activity).turnPage();
		}
	}

	@Override
	public void onError(String s, SpeechError speechError) {

	}
}

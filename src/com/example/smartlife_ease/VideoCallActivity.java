/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.smartlife_ease;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.exceptions.EMServiceNotReadyException;

import java.util.UUID;

import com.example.smartlife_ease.CameraHelper;
import com.example.smartlife.R;

public class VideoCallActivity extends CallActivity implements OnClickListener,OnTouchListener {

    private SurfaceView localSurface;
    private SurfaceHolder localSurfaceHolder;
    private static SurfaceView oppositeSurface;
    private SurfaceHolder oppositeSurfaceHolder;

    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private int streamID;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;

    EMVideoCallHelper callHelper;
    private TextView callStateTextView;

    private Handler handler = new Handler();
    private LinearLayout comingBtnContainer;
    private Button refuseBtn;
    private Button answerBtn;
    private Button hangupBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private TextView nickTextView;
    private Chronometer chronometer;
    private LinearLayout voiceContronlLayout;
    private RelativeLayout rootContainer;
    private RelativeLayout btnsContainer;
    private CameraHelper cameraHelper;
    private LinearLayout topContainer;
    private LinearLayout bottomContainer;
    private TextView monitorTextView;

    
    private ImageView back, img_01, img_02, img_top, img_a_01, img_a_02,img_a_03;;
	private RelativeLayout img_ab_01, img_ab_02, img_ab_03;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
        	finish();
        	return;
        }
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_video_call);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.main_doorbell_titlebar);
     // titlebar
     		back = (ImageView) findViewById(R.id.main_doorbell_btn_back);
     		back.setOnClickListener(this);
     		
     		// main
     		img_01 = (ImageView) findViewById(R.id.main_doorbell_img_01);
     		img_01.setOnTouchListener(this);
     		img_02 = (ImageView) findViewById(R.id.main_doorbell_img_02);
     		img_02.setOnTouchListener(this);

     		img_ab_01 = (RelativeLayout) findViewById(R.id.main_doorbell_above_img_re01);
     		img_ab_01.setOnTouchListener(this);
     		img_ab_02 = (RelativeLayout) findViewById(R.id.main_doorbell_above_img_re02);
     		img_ab_02.setOnTouchListener(this);
     		img_ab_03 = (RelativeLayout) findViewById(R.id.main_doorbell_above_img_re03);
     		img_ab_03.setOnTouchListener(this);
     		img_a_01 = (ImageView) findViewById(R.id.main_doorbell_above_img_01);
     		img_a_02 = (ImageView) findViewById(R.id.main_doorbell_above_img_02);
     		img_a_03 = (ImageView) findViewById(R.id.main_doorbell_above_img_03);
     		img_top = (ImageView) findViewById(R.id.main_doorbell_top_img);
        
        //HXSDKHelper.getInstance().isVideoCalling = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        rootContainer = (RelativeLayout) findViewById(R.id.root_layout);
        refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
        answerBtn = (Button) findViewById(R.id.btn_answer_call);
        hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
        btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        monitorTextView = (TextView) findViewById(R.id.tv_call_monitor);
        

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        rootContainer.setOnClickListener(this);

        msgid = UUID.randomUUID().toString();
        // ��ȡͨ���Ƿ�Ϊ���շ����
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");

        // ����ͨ����
        nickTextView.setText(username);

        // ��ʾ����ͼ���surfaceview
        localSurface = (SurfaceView) findViewById(R.id.local_surface);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);
        localSurfaceHolder = localSurface.getHolder();

        // ��ȡcallHelper,cameraHelper
        callHelper = EMVideoCallHelper.getInstance();
        cameraHelper = new CameraHelper(callHelper, localSurfaceHolder);

        // ��ʾ�Է�ͼ���surfaceview
        oppositeSurface = (SurfaceView) findViewById(R.id.opposite_surface);
        oppositeSurfaceHolder = oppositeSurface.getHolder();
        // ������ʾ�Է�ͼ���surfaceview
        callHelper.setSurfaceView(oppositeSurface);

        localSurfaceHolder.addCallback(new LocalCallback());
        oppositeSurfaceHolder.addCallback(new OppositeCallback());
     // �򿪾���
        muteImage.setImageResource(R.drawable.icon_mute_on);
        audioManager.setMicrophoneMute(true);
        isMuteState = true; 

        // ����ͨ������
        addCallStateListener();
        if (!isInComingCall) {// ����绰
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.beep, 1);

            //comingBtnContainer.setVisibility(View.INVISIBLE);
            //hangupBtn.setVisibility(View.VISIBLE);
            String st = getResources().getString(R.string.are_connected_to_each_other);
            callStateTextView.setText(st);

            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // �е绰����
            //voiceContronlLayout.setVisibility(View.INVISIBLE);
            //localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
        }
    }

    /**
     * ����SurfaceHolder callback
     * 
     */
    class LocalCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            cameraHelper.startCapture();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    /**
     * �Է�SurfaceHolder callback
     */
    class OppositeCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            callHelper.onWindowResize(width, height, format);
            if (!cameraHelper.isStarted()) {
                if (!isInComingCall) {
                    try {
                        // ������Ƶͨ��
                        EMChatManager.getInstance().makeVideoCall(username);
                        // ֪ͨcameraHelper����д������
                        cameraHelper.setStartFlag(true);
                    } catch (EMServiceNotReadyException e) {
                        Toast.makeText(VideoCallActivity.this, R.string.is_not_yet_connected_to_the_server , Toast.LENGTH_LONG).show();
                    }
                }

            } else {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

    }

    /**
     * ����ͨ��״̬����
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                // Message msg = handler.obtainMessage();
                switch (callState) {

                case CONNECTING: // �������ӶԷ�
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //callStateTextView.setText(R.string.are_connected_to_each_other);
                        	//Toast.makeText(getBaseContext(), "���ڴ򿪼��", Toast.LENGTH_SHORT).show();
                        }

                    });
                    break;
                case CONNECTED: // ˫���Ѿ���������
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //callStateTextView.setText(R.string.have_connected_with);
                        	callStateTextView.setVisibility(View.INVISIBLE);
                        }

                    });
                    break;

                case ACCEPTED: // �绰��ͨ�ɹ�
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                            } catch (Exception e) {
                            }
                            openSpeakerOn();
                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMChatManager.getInstance().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                            handsFreeImage.setImageResource(R.drawable.icon_speaker_on);
                            isHandsfreeState = true;
                            //chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // ��ʼ��ʱ
                            chronometer.start();
                           // nickTextView.setVisibility(View.INVISIBLE);
                            //callStateTextView.setText(R.string.in_the_call);
                            callingState = CallingState.NORMAL;
                            startMonitor();
                        }

                    });
                    break;
                case DISCONNNECTED: // �绰����
                    final CallError fError = error;
                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    saveCallRecord(1);
                                    Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                    animation.setDuration(800);
                                    rootContainer.startAnimation(animation);
                                    finish();
                                }

                            }, 200);
                        }

                        @Override
                        public void run() {
                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String s1 = getResources().getString(R.string.the_other_party_refused_to_accept);
                            String s2 = getResources().getString(R.string.connection_failure);
                            String s3 = getResources().getString(R.string.the_other_party_is_not_online);
                            String s4 = getResources().getString(R.string.the_other_is_on_the_phone_please);
                            String s5 = getResources().getString(R.string.the_other_party_did_not_answer);
                            
                            String s6 = getResources().getString(R.string.hang_up);
                            String s7 = getResources().getString(R.string.the_other_is_hang_up);
                            String s8 = getResources().getString(R.string.did_not_answer);
                            String s9 = getResources().getString(R.string.has_been_cancelled);
                            
                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUESD;
                                callStateTextView.setText(s1);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                //callStateTextView.setText(s2);
                            	Toast.makeText(getBaseContext(), "�򿪼���豸ʧ��", Toast.LENGTH_SHORT).show();
                            } else if (fError == CallError.ERROR_INAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                               // callStateTextView.setText(s3);
                                Toast.makeText(getBaseContext(), "�򿪼���豸ʧ��", Toast.LENGTH_SHORT).show();
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                //callStateTextView.setText(s4);
                                Toast.makeText(getBaseContext(), "�豸ռ�ã����Ժ��", Toast.LENGTH_SHORT).show();
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NORESPONSE;
                                callStateTextView.setText(s5);
                            } else {
                                if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                    } else {
                                        callStateTextView.setText(s7);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(s8);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCED;
                                            callStateTextView.setText(s9);
                                        } else {
                                            callStateTextView.setText(s6);
                                        }
                                    }
                                }
                            }
                            postDelayedCloseMsg();
                        }

                    });

                    break;

                default:
                    break;
                }

            }
        };
        EMChatManager.getInstance().addVoiceCallStateChangeListener(callStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_refuse_call: // �ܾ�����
            refuseBtn.setEnabled(false);
            if (ringtone != null)
                ringtone.stop();
            try {
                EMChatManager.getInstance().rejectCall();
            } catch (Exception e1) {
                e1.printStackTrace();
                saveCallRecord(1);
                finish();
            }
            callingState = CallingState.REFUESD;
            break;

        case R.id.btn_answer_call: // �����绰
            answerBtn.setEnabled(false);
            if (ringtone != null)
                ringtone.stop();
            if (isInComingCall) {
                try {
                    callStateTextView.setText("���ڽ���...");
                    EMChatManager.getInstance().answerCall();
                    cameraHelper.setStartFlag(true);

                    openSpeakerOn();
                    handsFreeImage.setImageResource(R.drawable.icon_speaker_on);
                    isAnswered = true;
                    isHandsfreeState = true;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    saveCallRecord(1);
                    finish();
                    return;
                }
            }
            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            voiceContronlLayout.setVisibility(View.VISIBLE);
            localSurface.setVisibility(View.VISIBLE);
            break;

        case R.id.btn_hangup_call: // �Ҷϵ绰
            hangupBtn.setEnabled(false);
            if (soundPool != null)
                soundPool.stop(streamID);
            chronometer.stop();
            endCallTriggerByMe = true;
            callStateTextView.setText(getResources().getString(R.string.hanging_up));
            try {
                EMChatManager.getInstance().endCall();
            } catch (Exception e) {
                e.printStackTrace();
                saveCallRecord(1);
                finish();
            }
            break;

        case R.id.iv_mute: // ��������
            if (isMuteState) {
                // �رվ���
                muteImage.setImageResource(R.drawable.icon_mute_normal);
                audioManager.setMicrophoneMute(false);
                isMuteState = false;
            } else {
                // �򿪾���
                muteImage.setImageResource(R.drawable.icon_mute_on);
                audioManager.setMicrophoneMute(true);
                isMuteState = true;
            }
            break;
        case R.id.main_doorbell_btn_back:  //titlebar����
        	hangupBtn.setEnabled(false);
            if (soundPool != null)
                soundPool.stop(streamID);
            chronometer.stop();
            endCallTriggerByMe = true;
            callStateTextView.setText(getResources().getString(R.string.hanging_up));
            try {
                EMChatManager.getInstance().endCall();
            } catch (Exception e) {
                e.printStackTrace();
                saveCallRecord(1);
                finish();
            }
			this.finish();
			break;
        case R.id.iv_handsfree: // ���Ὺ��
            if (isHandsfreeState) {
                // �ر�����
                handsFreeImage.setImageResource(R.drawable.icon_speaker_normal);
                closeSpeakerOn();
                isHandsfreeState = false;
            } else {
                handsFreeImage.setImageResource(R.drawable.icon_speaker_on);
                openSpeakerOn();
                isHandsfreeState = true;
            }
            break;
        case R.id.root_layout:
            if (callingState == CallingState.NORMAL) {
                if (bottomContainer.getVisibility() == View.VISIBLE) {
                    bottomContainer.setVisibility(View.GONE);
                    topContainer.setVisibility(View.GONE);
                    
                } else {
                    bottomContainer.setVisibility(View.VISIBLE);
                    topContainer.setVisibility(View.VISIBLE);

                }
            }

            break;
        default:
            break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //HXSDKHelper.getInstance().isVideoCalling = false;
        stopMonitor();
        try {
			callHelper.setSurfaceView(null);
			cameraHelper.stopCapture();
			oppositeSurface = null;
			cameraHelper = null;
		} catch (Exception e) {
		}
    }

    @Override
    public void onBackPressed() {
        EMChatManager.getInstance().endCall();
        callDruationText = chronometer.getText().toString();
        saveCallRecord(1);
        finish();
    }
    
    /**
     * ���㿪�����ԣ�ʵ��app��ȥ����ʾ����
     */
    void startMonitor(){
        new Thread(new Runnable() {
            public void run() {
                while(monitor){
                    runOnUiThread(new Runnable() {
                        public void run() {
                        	monitorTextView.setVisibility(View.INVISIBLE);
                            monitorTextView.setText("��x�ߣ�"+callHelper.getVideoWidth()+"x"+callHelper.getVideoHeight()
                                    + "\n�ӳ٣�" + callHelper.getVideoTimedelay()
                                    + "\n֡�ʣ�" + callHelper.getVideoFramerate()
                                    + "\n��������" + callHelper.getVideoLostcnt()
                                    + "\n���ر����ʣ�" + callHelper.getLocalBitrate()
                                    + "\n�Է������ʣ�" + callHelper.getRemoteBitrate());
                            
                        }
                    });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }
    
    void stopMonitor(){
        monitor = false;
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		// �ײ�
		case R.id.main_doorbell_img_01:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_01.setAlpha(160);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_01.setAlpha(255);
				//�Ҷϵ绰
		           hangupBtn.setEnabled(false);
		            if (soundPool != null)
		                soundPool.stop(streamID);
		            chronometer.stop();
		            endCallTriggerByMe = true;
		            callStateTextView.setText(getResources().getString(R.string.hanging_up));
		            try {
		                EMChatManager.getInstance().endCall();
		            } catch (Exception e) {
		                e.printStackTrace();
		                saveCallRecord(1);
		                finish();
		            }
			}
			break;
		case R.id.main_doorbell_img_02:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_02.setAlpha(160);
				img_top.setImageResource(R.drawable.ic_unprotect_state);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_02.setAlpha(255);
				img_top.setImageResource(R.drawable.ic_protect_state);
			}
			break;
		// �в�
		case R.id.main_doorbell_above_img_re01:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_01.setImageResource(R.drawable.ic_video_take_photo_pressed);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_01.setImageResource(R.drawable.ic_video_take_photo_normal);
			}

			break;
		case R.id.main_doorbell_above_img_re02:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_02.setImageResource(R.drawable.ic_video_talk_selected);
				
				  if (isMuteState) {
		                // �رվ���
		                muteImage.setImageResource(R.drawable.icon_mute_normal);
		                audioManager.setMicrophoneMute(false);
		                isMuteState = false;
		            }
				
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_02.setImageResource(R.drawable.ic_video_talk_normal);
				if (!isMuteState) {
		                // �򿪾���
		                muteImage.setImageResource(R.drawable.icon_mute_on);
		                audioManager.setMicrophoneMute(true);
		                isMuteState = true; 
		            }
				
			}
			break;
		case R.id.main_doorbell_above_img_re03:
			if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
				img_a_03.setImageResource(R.drawable.ic_video_record_selected);
			}
			if (arg1.getAction() == MotionEvent.ACTION_UP) {
				img_a_03.setImageResource(R.drawable.ic_video_record_normal);
			}
			break;

		default:
			break;
		}

		return false;
	}

}
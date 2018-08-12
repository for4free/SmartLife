package com.example.smartlife;

import java.util.List;
import java.util.Map;

import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.smartlife.List01_Dialog.List01_DialogListener;
import com.example.smartlife.List02_Dialog.List02_DialogListener;
import com.example.smartlife.List03_Dialog.List03_DialogListener;
import com.example.smartlife.List04_Dialog.List04_DialogListener;
import com.example.smartlife.PinnedHeaderExpandableListView_Sockt.HeaderAdapter;
import com.example.smartlife_api.GetData_Api;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class PinnedHeaderExpandableAdapter_Sockt extends BaseExpandableListAdapter implements HeaderAdapter {
	//private String[][] childrenData;
	//private String[] groupData;
	private Context context;
	private PinnedHeaderExpandableListView_Sockt listView;
	private LayoutInflater inflater;
	private String id;
	public List<String> groupList;
	public Map<String,List<Map<String,String>>> childMap;
	private String[] dataSplit;
	//private TextView dialogtText;
	public static Handler handler = new Handler();
	
	public PinnedHeaderExpandableAdapter_Sockt(Map<String,List<Map<String,String>>> childMap,List<String> groupList, Context context,PinnedHeaderExpandableListView_Sockt listView) {
		this.groupList = groupList;
		this.childMap = childMap;
		this.context = context;
		this.listView = listView;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		//return childrenData[groupPosition][childPosition];
		String groupName = groupList.get(groupPosition);
		Map childName = childMap.get(groupName).get(childPosition);
		return childName;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
		View view = null;
		
		if (convertView != null) {
			view = convertView;
		} else {
			view = createChildrenView();
		}
		TextView text = (TextView) view.findViewById(R.id.childto_sockt);
		final String groupName = groupList.get(groupPosition);
		//text.setText(childrenData[groupPosition][childPosition]);
		//dialogtText  = (TextView) view.findViewById(R.id.dialog_list01_txt);
		String childName = childMap.get(groupName).get(childPosition).get("name");
		text.setText(childName);
		
		LinearLayout touch = (LinearLayout) view.findViewById(R.id.main_sockt_touch);
		touch.setOnTouchListener(new OnTouchListener() {
			
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction() == MotionEvent.ACTION_UP){
					SharedPreferences sharedPreferences = context.getSharedPreferences("NEWDATA", 0);
					final Editor editor = sharedPreferences.edit();
					
					final String did = childMap.get(groupName).get(childPosition).get("id");
					final String newdata = childMap.get(groupName).get(childPosition).get("newdata");
					final String type = childMap.get(groupName).get(childPosition).get("type");
					final String name = childMap.get(groupName).get(childPosition).get("name");
					//窗帘
					if(type.equals("6")){
						
						List01_Dialog dialog = new List01_Dialog(name,newdata,context, R.style.Dialog, new List01_DialogListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//dialogtText.setText(childMap.get(groupName).get(childPosition).get("newdata"));
								switch (v.getId()) {
								/*case R.id.dialog_list01_but_close:
									updateData(uid, ""+6, ""+0);
									new Thread(back).start();
									break;*/
								case R.id.dialog_list01_but_open:
									if(newdata.equals("0")){
										updateData(did, type, "1");	
									}else{
										updateData(did, type, "0");
									}
									
									new Thread(back).start();
									break;
								default:
									break;
								}
							}
						});
						dialog.show();
						//end
					}
					//插座
					else if(type.equals("a1")){
						if(newdata.length() == 1){
							dataSplit = new String[2];
							dataSplit[0] = "0";
							dataSplit[1] = "0";
						}else{
							dataSplit = newdata.split("_");
						}
						
						List02_Dialog dialog = new List02_Dialog(name,newdata,context, R.style.Dialog, new List02_DialogListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//dialogtText.setText(childMap.get(groupName).get(childPosition).get("newdata"));
								switch (v.getId()) {
								/*case R.id.dialog_list01_but_close:
									updateData(uid, ""+6, ""+0);
									new Thread(back).start();
									break;*/
								case R.id.dialog_list02_but_open:
									if(dataSplit[0].equals("0")){
										updateData(did, type, 1+"_"+dataSplit[1]);	
									}else{
										updateData(did, type, 0+"_"+dataSplit[1]);
									}
									
									new Thread(back).start();
									break;
								default:
									break;
								}
							}
						});
						dialog.show();
						//end
					}
					//电视
					else if(type.equals("a3")){
						if(newdata.length() == 1){
							dataSplit = new String[3];
							dataSplit[0] = "0";
							dataSplit[1] = "0";
							dataSplit[2] = "0";
						}else{
							dataSplit = newdata.split("_");
						}
						
						
						List03_Dialog dialog = new List03_Dialog(name,newdata,context, R.style.Dialog, new List03_DialogListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//dialogtText.setText(childMap.get(groupName).get(childPosition).get("newdata"));
								switch (v.getId()) {
								/*case R.id.dialog_list01_but_close:
									updateData(uid, ""+6, ""+0);
									new Thread(back).start();
									break;*/
								case R.id.dialog_list03_power:
									if(dataSplit[0].equals("0")){
										updateData(did, type, 1+"_"+dataSplit[1]+"_"+dataSplit[2]);	
									}else{
										updateData(did, type, 0+"_"+dataSplit[1]+"_"+dataSplit[2]);
										new Thread(back).start();
									}
									break;
								case R.id.dialog_list03_channel01:
									if(dataSplit[0].equals("1")){
										updateData(did, type, 1+"_"+(Integer.valueOf(dataSplit[1]).intValue()+1)+"_"+dataSplit[2]);
									}
									break;
								case R.id.dialog_list03_channel02:
									if(dataSplit[0].equals("1")){
										updateData(did, type, 1+"_"+(Integer.valueOf(dataSplit[1]).intValue()-1)+"_"+dataSplit[2]);
									}
									break;
								case R.id.dialog_list03_idvolume01:
									if(dataSplit[0].equals("1")){
										updateData(did, type, 1+"_"+dataSplit[1]+"_"+(Integer.valueOf(dataSplit[2]).intValue()+1));
									}
									break;
								case R.id.dialog_list03_idvolume02:
									if(dataSplit[0].equals("1")){
										updateData(did, type, 1+"_"+dataSplit[1]+"_"+(Integer.valueOf(dataSplit[2]).intValue()-1));
									}
									break;
								default:
									break;
								}
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Log.e("111", "2222");
										try {
											Thread.sleep(1100);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										String new_data = childMap.get(groupName).get(childPosition).get("newdata");
										dataSplit = new_data.split("_");
										editor.putString("power", dataSplit[0]);
										editor.putString("data01", dataSplit[1]);
										editor.putString("data02", dataSplit[2]);
										editor.commit();
										Message msg = new Message();
										msg.what = 1;
										handler.sendMessage(msg);
									}
								}).start();
							}
						});
						dialog.show();
						//end
					}
					//空调
					else if(type.equals("a2")){
						if(newdata.length() == 1){
							dataSplit = new String[3];
							dataSplit[0] = "0";
							dataSplit[1] = "0";
							dataSplit[2] = "0";
						}else{
							dataSplit = newdata.split("_");
						}
						
						List04_Dialog dialog = new List04_Dialog(name,newdata,context, R.style.Dialog, new List04_DialogListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//dialogtText.setText(childMap.get(groupName).get(childPosition).get("newdata"));
								switch (v.getId()) {
								/*case R.id.dialog_list01_but_close:
									updateData(uid, ""+6, ""+0);
									new Thread(back).start();
									break;*/
								case R.id.dialog_list04_power:
									if(dataSplit[0].equals("0")){
										updateData(did, type, "1"+"_"+dataSplit[1]+"_"+dataSplit[2]);	
									}else if(dataSplit[0].equals("1")){
										updateData(did, type, "0"+"_"+dataSplit[1]+"_"+dataSplit[2]);
										new Thread(back).start();
									}
									//new Thread(back).start();
								case R.id.dialog_list04_temp01:
									if(dataSplit[0].equals("1")){
										updateData(did, type, 1+"_"+(Integer.valueOf(dataSplit[1]).intValue()+1)+"_"+dataSplit[2]);	
									}
									break;
								case R.id.dialog_list04_temp02:
									if(dataSplit[0].equals("1")){
										updateData(did, type, 1+"_"+(Integer.valueOf(dataSplit[1]).intValue()-1)+"_"+dataSplit[2]);	
									}
									break;
								default:
									break;
								}
								
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Log.e("111", "2222");
										try {
											Thread.sleep(1100);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										String new_data = childMap.get(groupName).get(childPosition).get("newdata");
										dataSplit = new_data.split("_");
										editor.putString("power", dataSplit[0]);
										editor.putString("data01", dataSplit[1]);
										editor.putString("data02", dataSplit[2]);
										editor.commit();
										Message msg = new Message();
										msg.what = 1;
										handler.sendMessage(msg);
									}
								}).start();
							}
						});
						dialog.show();
						//end
					}
					//其他
					else{
						List01_Dialog dialog = new List01_Dialog(name,newdata,context, R.style.Dialog, new List01_DialogListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									//dialogtText.setText(childMap.get(groupName).get(childPosition).get("newdata"));
									switch (v.getId()) {
									/*case R.id.dialog_list01_but_close:
										updateData(uid, ""+6, ""+0);
										new Thread(back).start();
										break;*/
									case R.id.dialog_list01_but_open:
										
										if(newdata.equals("0")){
											updateData(did, type, ""+1);	
										}else{
											updateData(did, type, ""+0);
										}
										
										new Thread(back).start();
										break;
									default:
										break;
									}
								}
							});
							dialog.show();
							//end
					}
				}
				return false;
			}
		});
		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		//return childrenData[groupPosition].length;
		try {
			String groupName = groupList.get(groupPosition);
			int childCount = childMap.get(groupName).size();
			return childCount;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		//return groupData[groupPosition];
		String groupName = groupList.get(groupPosition);
		return groupName;
	}

	@Override
	public int getGroupCount() {
		//return groupData.length;
		return groupList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = createGroupView();
		}

		ImageView iv = (ImageView) view.findViewById(R.id.groupIcon_sockt);

		if (isExpanded) {
			iv.setImageResource(R.drawable.down);
		} else {
			iv.setImageResource(R.drawable.right);
		}

		TextView text = (TextView) view.findViewById(R.id.groupto_sockt);
		//text.setText(groupData[groupPosition]);
		text.setText(groupList.get(groupPosition));
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private View createChildrenView() {
		return inflater.inflate(R.layout.main_sockt_child, null);
	}

	private View createGroupView() {
		return inflater.inflate(R.layout.main_sockt_grouphead, null);
	}

	@Override
	public int getHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1&& !listView.isGroupExpanded(groupPosition)) {
			return PINNED_HEADER_GONE;
		} else {
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureHeader(View header, int groupPosition,int childPosition, int alpha) {
		//String groupData = this.groupData[groupPosition];
		String groupData = this.groupList.get(groupPosition);
		((TextView) header.findViewById(R.id.groupto_sockt)).setText(groupData);

	}

	private SparseIntArray groupStatusMap = new SparseIntArray();

	@Override
	public void setGroupClickStatus(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getGroupClickStatus(int groupPosition) {
		if (groupStatusMap.keyAt(groupPosition) >= 0) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}
	//模拟返回键
	Runnable back = new Runnable() {
	
		@Override
		public void run() {
			// TODO Auto-generated method stubnew Thread () {
		
			try {
				Instrumentation inst= new Instrumentation();
				inst.sendKeyDownUpSync(KeyEvent. KEYCODE_BACK);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	//更新数据
	public void updateData(String getId,String getType,String newdata){
		SharedPreferences sharedPreferences = context.getSharedPreferences("LOGIN", 0);
		id = sharedPreferences.getString("id", "");
		
    	HttpUtils http = new HttpUtils(500);//9s超时
    	http.configCurrentHttpCacheExpiry(0);
		http.send(HttpMethod.GET, App.address+"CSetData.php?uid="+id+"&getType="+getType+"&getId="+getId+"&newData="+newdata+"&flag=1", new RequestCallBack<String>() {

    		@Override
			public void onFailure(HttpException arg0, String arg1) {
    			Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				//Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
				GetData_Api content = (GetData_Api) JSONObject.parseObject(arg0.result, GetData_Api.class);
				if (content.getStatus().equals("402")|| content.getStatus().equals("401")) {
					//出错
					Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show();
				} else if (content.getStatus().equals("403")) {
					Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
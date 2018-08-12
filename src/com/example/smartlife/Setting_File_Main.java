package com.example.smartlife;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class Setting_File_Main extends ListActivity implements android.view.View.OnClickListener{  
    //private static final String ROOT_PATH = "/sdcard/SmartLife/";  
	private static final String ROOT_PATH = "/sdcard/SmartLife/";  
    private ImageView btn_back;
    //�洢�ļ�����  
    private ArrayList<String> names = null;  
    //�洢�ļ�·��  
    private ArrayList<String> paths = null;  
    private View view;  
    private EditText editText;  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.left_file_main);  
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.left_file_main_titlebar);
        //��ʾ�ļ��б�  
        showFileDir(ROOT_PATH);  
        
        
        btn_back = (ImageView) findViewById(R.id.left_file_title_btn);
        btn_back.setOnClickListener(this);
    }  
    private void showFileDir(String path){  
        names = new ArrayList<String>();  
        paths = new ArrayList<String>();  
        File file = new File(path);  
        File[] files = file.listFiles();  
          
        //�����ǰĿ¼���Ǹ�Ŀ¼  
        if (!ROOT_PATH.equals(path)){  
            names.add("@1");  
            paths.add(ROOT_PATH);  
              
            names.add("@2");  
            paths.add(file.getParent());  
        }  
        //��������ļ�  
        for (File f : files){  
            names.add(f.getName());  
            paths.add(f.getPath());  
        }  
        this.setListAdapter(new FileAdapter(this,names, paths));  
    }  
    @Override  
    protected void onListItemClick(ListView l, View v, int position, long id) {  
        String path = paths.get(position);  
        File file = new File(path);  
        // �ļ����ڲ��ɶ�  
        if (file.exists() && file.canRead()){  
            if (file.isDirectory()){  
                //��ʾ��Ŀ¼���ļ�  
                showFileDir(path);  
            }  
            else{  
                //�����ļ�  
                fileHandle(file);  
            }  
        }  
        //û��Ȩ��  
        else{  
            Resources res = getResources();  
            new AlertDialog.Builder(this).setTitle("Message").setMessage("������what?").setPositiveButton("OK",new OnClickListener() {  
                @Override  
                public void onClick(DialogInterface dialog, int which) {  
                      
                }  
            }).show();  
        }  
        super.onListItemClick(l, v, position, id);  
    }  
    //���ļ�������ɾ��  
    private void fileHandle(final File file){  
        OnClickListener listener = new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                // ���ļ�  
                if (which == 0){  
                    openFile(file);  
                }  
                //�޸��ļ���  
                else if(which == 1){  
                    LayoutInflater factory = LayoutInflater.from(Setting_File_Main.this);  
                    view = factory.inflate(R.layout.left_file_rename, null);  
                    editText = (EditText)view.findViewById(R.id.file_editText);  
                    editText.setText(file.getName());  
                      
                    OnClickListener listener2 = new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            // TODO Auto-generated method stub  
                            String modifyName = editText.getText().toString();  
                            final String fpath = file.getParentFile().getPath();  
                            final File newFile = new File(fpath + "/" + modifyName);  
                            if (newFile.exists()){  
                                //�ų�û���޸����  
                                if (!modifyName.equals(file.getName())){  
                                    new AlertDialog.Builder(Setting_File_Main.this)  
                                    .setTitle("ע��!")  
                                    .setMessage("�ļ����Ѵ��ڣ��Ƿ񸲸ǣ�")  
                                    .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
                                        @Override  
                                        public void onClick(DialogInterface dialog, int which) {  
                                            if (file.renameTo(newFile)){  
                                                showFileDir(fpath);  
                                                displayToast("�������ɹ���");  
                                            }  
                                            else{  
                                                displayToast("������ʧ�ܣ�");  
                                            }  
                                        }  
                                    })  
                                    .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
                                        @Override  
                                        public void onClick(DialogInterface dialog, int which) {  
                                              
                                        }  
                                    })  
                                    .show();  
                                }  
                            }  
                            else{  
                                if (file.renameTo(newFile)){  
                                    showFileDir(fpath);  
                                    displayToast("�������ɹ���");  
                                }  
                                else{  
                                    displayToast("������ʧ�ܣ�");  
                                }  
                            }  
                        }  
                    };  
                    AlertDialog renameDialog = new AlertDialog.Builder(Setting_File_Main.this).create();  
                    renameDialog.setView(view);  
                    renameDialog.setButton("ȷ��", listener2);  
                    renameDialog.setButton2("ȡ��", new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            // TODO Auto-generated method stub  
                              
                        }  
                    });  
                    renameDialog.show();  
                }  
                //ɾ���ļ�  
                else{  
                    new AlertDialog.Builder(Setting_File_Main.this)  
                    .setTitle("ע��!")  
                    .setMessage("ȷ��Ҫɾ�����ļ���")  
                    .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            if(file.delete()){  
                                //�����ļ��б�  
                                showFileDir(file.getParent());  
                                displayToast("ɾ���ɹ���");  
                            }  
                            else{  
                                displayToast("ɾ��ʧ�ܣ�");  
                            }  
                        }  
                    })  
                    .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                              
                        }  
                    }).show();  
                }  
            }  
        };  
        //ѡ���ļ�ʱ��������ɾ�ò���ѡ��Ի���  
        String[] menu = {"���ļ�","������","ɾ���ļ�"};  
        new AlertDialog.Builder(Setting_File_Main.this)  
        .setTitle("��ѡ��Ҫ���еĲ���!")  
        .setItems(menu, listener)  
        .setPositiveButton("ȡ��", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                  
            }  
        }).show();  
    }  
    //���ļ�  
    private void openFile(File file){  
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.setAction(android.content.Intent.ACTION_VIEW);  
          
        String type = getMIMEType(file);  
        intent.setDataAndType(Uri.fromFile(file), type);  
        startActivity(intent);  
    }  
    //��ȡ�ļ�mimetype  
    private String getMIMEType(File file){  
        String type = "";  
        String name = file.getName();  
        //�ļ���չ��  
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();  
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")){  
            type = "audio";  
        }  
        else if(end.equals("mp4") || end.equals("3gp")) {  
            type = "video";  
        }  
        else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")){  
            type = "image";  
        }  
        else {  
            //����޷�ֱ�Ӵ򿪣������б����û�ѡ��  
            type = "*";  
        }  
        type += "/*";  
        return type;  
    }  
    private void displayToast(String message){  
        Toast.makeText(Setting_File_Main.this, message, Toast.LENGTH_SHORT).show();  
    }
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.left_file_title_btn:
			
			this.finish();
			
			break;

		default:
			break;
		}
		
		
	}  
}  

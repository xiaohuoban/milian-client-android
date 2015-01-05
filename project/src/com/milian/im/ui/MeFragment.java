package com.milian.im.ui;

import java.io.File;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;  
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;  
import android.view.View.OnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.database.Cursor; 

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Date;
import java.text.DateFormat;

import com.milian.im.entry.AccountML;
import com.milian.im.entry.ContactML;
import com.milian.im.service.NotifyBroadcast;
import com.milian.im.ui.ContactInfoActivity.NotifyReceiver;


class Tools {
	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}
	}
}


public class MeFragment extends Fragment {
	

	private ImageView faceImage;

	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	private static  String IMAGE_FILE_NAME = "faceImage.jpg";
	private static  String IMAGE_TMP_NAME = "faceImage.jpg";
	private static int IMAGE_CNT = 0;

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	MainActivity activity ;
	View view;
	int state;
	TextView stateTextView;
	LayoutInflater inflater;
	 EditText aliasname ;
	private AccountML account;
	private NotifyReceiver notifyReceiver;
	private boolean editable = false;
	//private ContactML contact;
	
	public MeFragment() {
		
		//initBroadCast();
	}

	public class NotifyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) { 
			if ( intent.getAction().equals(NotifyBroadcast.NOTIFY_TYPE_MESSAGE)) {
		        int msgID = intent.getIntExtra(NotifyBroadcast.MSG_ID,  0);
        
		        switch ( msgID) {
			        case NotifyBroadcast.CONNECT_SUCCESS_MSG:
			        	account.setState(AccountML.STATE_CONNECTING);
			        	break;
			        case NotifyBroadcast.CONNECT_FAIL_MSG:
			        	account.setState(AccountML.STATE_CONNECT_FAILED);
			        	break;
			        	
			        case NotifyBroadcast.ACCOUNT_REMOVE_MSG:
			        	break;
			        	
			        case NotifyBroadcast.ACCOUNT_ADD_MSG:
			        	
			    		
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_SUCCESS_MSG:
			        	account.setState(AccountML.STATE_ON_LINE);
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_FAIL_MSG:
			        	account.setState(AccountML.STATE_LOGIN_FAIL);
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_FAIL_PASSWORD_ERROR_MSG:
			        	account.setState(AccountML.STATE_LOGIN_PASSWORD_ERROR);
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_RESOURCE_CONFLICT_MSG:
			        	account.setState(AccountML.STATE_LOGIN_RESOURCE_CONFLICT);
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_USER_NOT_EXIST_MSG:
			        	account.setState(AccountML.STATE_LOGIN_USER_NOT_EXIST);
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_XMPP_ERROR_MSG:
			        	account.setState(AccountML.STATE_LOGIN_FAIL);
			        	break;
			        case NotifyBroadcast.MESSAGE_NEW_RECEIVE__MSG:
			        	
			        	break;
			        	
			        case NotifyBroadcast.CONTACT_ADD__MSG:
			        	break;
			        case NotifyBroadcast.CONTACT_UPDATE__MSG:
			        	break;      
			        case NotifyBroadcast.CONTACT_REMOVE__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_JOIN_SUCCESS__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_JOIN_FAIL__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_LEAVE_SUCCESS__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_LEAVE_FAIL__MSG:
			        	break;      
			        	
			        	default:
			        		Log.e("chatActivity", "receive a message: " + msgID);
			        		break;
		
		        }
		        stateTextView.setText(account.getState());
			}
			
	    } 
	}
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//initMeView();
		activity = (MainActivity)getActivity();
		//initBroadCast();
		account = activity.getAccount();
	}
	
	public void initBroadCast()
	{
		notifyReceiver = new NotifyReceiver();
		IntentFilter intentFilter = new IntentFilter(NotifyBroadcast.NOTIFY_TYPE_MESSAGE );
		Log.d("milian", "MeFragment registerReceiver");
		getActivity().registerReceiver( notifyReceiver , intentFilter);
	}
	
	public void onAttach(Activity activity) {
        super.onAttach(activity);
		initBroadCast();
	}
	public void initMeView()
	{
		//ac/ivity.requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题
		//activity.setContentView(R.layout.main);
		//switchAvatar = (RelativeLayout) findViewById(R.id.switch_face_rl);
		faceImage = (ImageView) view. findViewById(R.id.me_header_image);
		// 设置事件监听
		faceImage.setOnClickListener(listener);
		
		final EditText username = (EditText) view. findViewById(R.id.me_username);
		aliasname = (EditText) view. findViewById(R.id.me_name);
		final EditText password = (EditText) view. findViewById(R.id.me_password);
		final EditText server = (EditText) view. findViewById(R.id.me_server);
		final Button  modify = (Button)  view. findViewById(R.id.me_modify);
		stateTextView = (TextView) view. findViewById(R.id.me_state);
		stateTextView.setText(account.getState());
		username.setText(  ((MainActivity)getActivity()).getAccount().getUsername()  );
		password.setText(   ((MainActivity)getActivity()).getAccount().getPassword()  );
		server.setText(   ((MainActivity)getActivity()).getAccount().getDomain()  );
		aliasname.setText("上帝");
		username.setEnabled(false);
		password.setEnabled(false);
		server.setEnabled(false);
		modify.setText( "修改账号" );
		
		aliasname.addTextChangedListener(new TextWatcher() {
            
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }
            
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }
            
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            	  if (account.getAliasName().equals( aliasname.getText().toString())) {
            		  Log.v( "milian", "name is not changed, do nothing" );
            		  
            	  } else {
            		  account.setAliasName(aliasname.getText().toString());
            		  ((MainActivity)activity).setAccountMLName(account, account.getAliasName());
            		  Toast.makeText(activity, "name change to " + account.getAliasName(), Toast.LENGTH_SHORT).show();
            		  
            	  }
            }
        });
		
		modify.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (editable == true) {
					
					if (username.getText().toString().equals("")  ||
							server.getText().toString().equals("")  ||
							password.getText().toString().equals("")  ) {
						Toast.makeText(activity, "请确认输入非空内容 ！", Toast.LENGTH_SHORT).show();
						return ;
					}
					
					editable = false;
					username.setEnabled(false);
					password.setEnabled(false);
					server.setEnabled(false);
					modify.setText( "修改账号" );
					commitAccount();
				} else {
					editable = true;
					username.setEnabled(true);
					password.setEnabled(true);
					server.setEnabled(true);
					 modify.setText( "提交" );
					
				}
				
			}
		});
		
		 
	}
	
	public void commitAccount()
	{
		final EditText username = (EditText) view. findViewById(R.id.me_username);
		final EditText password = (EditText) view. findViewById(R.id.me_password);
		final EditText server = (EditText) view. findViewById(R.id.me_server);
		AccountML newAccount = new AccountML(username.getText().toString() ,
				password.getText().toString(),
				server.getText().toString(),
				"mobile",
				5222  );
		((MainActivity)activity).updateAccountML(newAccount);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{  
		view = inflater.inflate(R.layout.me, container, false);
		this.inflater = inflater;
		 initMeView();
        return view;
    }
	
 
	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog();
		}
	};

	/**
	 * 显示选择对话框
	 */
	
	String Path_Relative_Camera = "/milian/photo/";
	private void showDialog() {

		new AlertDialog.Builder(activity)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {
								String fileDir = Environment.getExternalStorageDirectory().toString() + Path_Relative_Camera;
								
								File f = new File(fileDir);
                                if (!f.exists()) {
                                        f.mkdirs();// 创建目录
                                       // f.createNewFile();// 创建文件
                                       // Log.i( "milian","20 ");
                                }
                               
                                IMAGE_TMP_NAME = fileDir + SystemClock.currentThreadTimeMillis() + ".jpg";
                                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                                Uri.fromFile(new File(fileDir, SystemClock.currentThreadTimeMillis() + ".jpg")));
                               // File file = new File(fileDir);
                                /*
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory() + IMAGE_FILE_NAME
												)));   */
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		//结果码不等于取消时候
		if (resultCode== activity.RESULT_OK) {
			
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				Bitmap bitmap = null;
				Uri uri_DCIM = null;
				String DCIMPath = "";
				if (Tools.hasSdcard()) {

					Log.i( "milian","has sd card ");
					/*
					IMAGE_FILE_NAME = "header" + System.currentTimeMillis()+ ".jpg";
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									,IMAGE_FILE_NAME);
					 Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()				+ IMAGE_FILE_NAME);  
					Log.i( "milian","display bitmap ");
					if ( bmp == null ) {
						Log.i( "milian"," bitmap is null ");
						 
					}
					faceImage.setImageBitmap(bmp);  
					
					*/
					if ( data != null ) {

	                    if (data != null  && data.getData() != null) {
	                            uri_DCIM = data.getData();
	                    } else {
	                            uri_DCIM = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	                    }
	                    Cursor cr = activity.getContentResolver().query(uri_DCIM,
	                                    new String[] { MediaStore.Images.Media.DATA }, null, null,
	                                    MediaStore.Images.Media.DATE_MODIFIED + " desc");
	                    if(cr != null) {
			                    if (cr.moveToNext()) {
			                            DCIMPath = cr.getString(cr.getColumnIndex(MediaStore.Images.Media.DATA));
			                    }
			                    cr.close();
	                    }
	                    // 保存图片到指定路径
	                    
	                    if (data != null  && data.getExtras() != null) {
	                            bitmap = (Bitmap) data.getExtras().get("data");
	                    } else {
	                            bitmap = BitmapFactory.decodeFile(DCIMPath);
	                    }
				} else {
					DCIMPath = IMAGE_TMP_NAME;
					bitmap = BitmapFactory.decodeFile(DCIMPath);
				}

                    String PhotoPath= Environment.getExternalStorageDirectory().toString() + Path_Relative_Camera;

                    String PhotoFullPath = PhotoPath + "/header.jpg";

					 FileOutputStream fos = null;
	                    File file = null;
	                    try {

	                            file = new File(PhotoFullPath);

	                            if (!file.exists()) {

	                                    File f = new File(PhotoPath);
	                                    if (!f.exists()) {
	                                            f.mkdirs();// 创建目录
	                                    }
	                                    file.createNewFile();// 创建文件
	                            }
	                            fos = new FileOutputStream(PhotoFullPath);
	                           // bitmap = Bitmap.createScaledBitmap(bitmap, Width_Camera, Height_Camera,  false);
	                            if (fos != null) {
		                            if ( bitmap != null)
		                            	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		                            fos.flush();
		                            fos.close();
	                            }
	                    } catch (FileNotFoundException e) {
	                            // TODO Auto-generated catch block
	                            Log.v("milian", this + "====找不到文件" + PhotoFullPath + "！" + e);
	                    } catch (IOException e) {
	                            // TODO Auto-generated catch block
	                    	Log.v("milian", this + "====拍照图片保存发生异常！" + e);
	                    }
	                    
	                    // 删除系统拍照图片
	                    if (!DCIMPath.equals("")) {
	                            boolean bool_delete = new File(DCIMPath).delete();
	                            System.out.println(this + "====删除系统拍照图片" + DCIMPath + "结果::" + bool_delete);
	                    } else {
	                            System.out.println(this + "====系统拍照图片路径为空！不需删除！");
	                    }

						if( file != null) {
							startPhotoZoom(Uri.fromFile(file));
						}
						if( fos != null) {
							try {
							fos.close();
							} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
						} else {
							Log.i( "milian","has no sd card ");
							Toast.makeText(activity, "未找到存储卡，无法存储照片！",
									Toast.LENGTH_LONG).show();
						}
					
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
					
				} else {
					
					Log.i( "milian","data is null ");
				}
				break;
				
			 default:
				 Log.i( "milian","default result code ");
				break;
			}
			
			
		}  else  {
			Log.i( "milian"," return fail ");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		Log.i( "milian","start com.android.camera.action.CROP ");
		startActivityForResult(intent, RESULT_REQUEST_CODE);
		Log.i( "milian","End com.android.camera.action.CROP ");
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			faceImage.setImageDrawable(drawable);
		}
	}
	
	public void updateAccount() {
		account = activity.getAccount();
    	final EditText aliasname = (EditText) view. findViewById(R.id.me_name);
		final EditText username = (EditText) view. findViewById(R.id.me_username);
		final EditText password = (EditText) view. findViewById(R.id.me_password);
		final EditText server = (EditText) view. findViewById(R.id.me_server);
		aliasname.setText(account.getAliasName());
		username.setText(account.getUsername());
		password.setText(account.getPassword());
		server.setText(account.getDomain());
	}
}

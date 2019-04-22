package com.xvoice.postdemo;


import  com.xvoice.postdemo.AudioPlayerUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.et_word)
    EditText Et_word;
    @BindView(R.id.bt_upload)
    Button Bt_upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.bt_upload)
    public void clickUpload() {
         


        final String res=Et_word.getText().toString();
        final String src_text=res;
        //final String VerifyValue="12";
        //final String Id="fs13a2ztDd2gK1UHqXZa";
        new Thread(){
            public void run() {
                try{
                    JSONObject rjson=httpUrlConnPost(src_text);
                    if(rjson==new JSONObject()){
                        return;
                    }else
                    {
                        AudioPlayerUtils.newInstance().playBase64(ConvertAndAudio(rjson));
                        AudioPlayerUtils.newInstance().stop();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();


        System.out.println("点击了按钮");

    }

    //返回一个json数据，包含wav加密信息
    public JSONObject httpUrlConnPost(String src_text){
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL("http://192.168.XX.xx:8891/synthesis");//公司不能暴露了
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(3000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            //urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setReadTimeout(3000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
            urlConnection.setRequestMethod("POST");//设置请求的方式
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型
            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
            JSONObject json = new JSONObject();//创建json对象
            json.put("src_text", src_text);//使用URLEncoder.encode对特殊和不可见字符进行编码
           // json.put("src_text", URLEncoder.encode(src_text, "UTF-8"));//使用URLEncoder.encode对特殊和不可见字符进行编码
            //json.put("VerifyValue", URLEncoder.encode(VerifyValue, "UTF-8"));//把数据put进json对象中
            //json.put("Id", URLEncoder.encode(Id, "UTF-8"));//把数据put进json对象中
            String json_str = json.toString();//把JSON对象按JSON的编码格式转换为字符串
            System.out.println(json_str+"打印了json");
            //-------------使用字节流发送数据--------------
            //OutputStream out = urlConnection.getOutputStream();
            //BufferedOutputStream bos = new BufferedOutputStream(out);//缓冲字节流包装字节流
            //byte[] bytes = jsonstr.getBytes("UTF-8");//把字符串转化为字节数组
            //bos.write(bytes);//把这个字节数组的数据写入缓冲区中
            //bos.flush();//刷新缓冲区，发送数据
            //out.close();
            //bos.close();
            //------------字符流写入数据------------
            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            bw.write(json_str);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            out.close();
            bw.close();//使用完关闭

            if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){//得到服务端的返回码是否连接成功
                //------------字节流读取服务端返回的数据------------
                //InputStream in = urlConnection.getInputStream();//用输入流接收服务端返回的回应数据
                //BufferedInputStream bis = new BufferedInputStream(in);//高效缓冲流包装它，这里用的是字节流来读取数据的，当然也可以用字符流
                //byte[] b = new byte[1024];
                //int len = -1;
                //StringBuffer buffer = new StringBuffer();//用来接收数据的StringBuffer对象
                //while((len=bis.read(b))!=-1){
                //buffer.append(new String(b, 0, len));//把读取到的字节数组转化为字符串
                //}
                //in.close();
                //bis.close();
                //Log.d("zxy", buffer.toString());//{"json":true}
                //JSONObject rjson = new JSONObject(buffer.toString());//把返回来的json编码格式的字符串数据转化成json对象
                //------------字符流读取服务端返回的数据------------
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while((str = br.readLine())!=null){//BufferedReader特有功能，一次读取一行数据
                    buffer.append(str);
                }
                in.close();
                br.close();
                JSONObject rjson = new JSONObject(buffer.toString());

                Log.d("返回的json数据", "rjson="+rjson);//rjson=rjson={"SynthesisResult":"data:audio\/wav;base64,UklGRq6r}
                System.out.println("Connection_success:  "+urlConnection.getResponseCode());
                return rjson;
            }else{
                System.out.println("Connection_FAILED");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Try_FAILED");
        }finally{
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
        return new JSONObject();
    }
    public String ConvertAndAudio (JSONObject rjson)throws Exception
    {
        if(rjson.has("SynthesisResult")) {
                    String s=(String)rjson.get("SynthesisResult");
                    int index=s.indexOf("base64,");
                    s=s.substring(index+7);
                    System.out.println(s);
                    return s;

        }
        return new String();
    }

}
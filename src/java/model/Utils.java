package model;

public class Utils {

    public static void sendFCMNotification(String to, String title, String msgBody, String image) {

        FCMThread fCMThread = new FCMThread(to, title, msgBody, image);
        fCMThread.setPriority(Thread.MAX_PRIORITY);
        fCMThread.start();
    }

}

class FCMThread extends Thread {
    
    String to;
    String title;
    String msgBody;
    String image;

    public FCMThread(String to, String title, String msgBody, String image) {
        this.to = to;
        this.title = title;
        this.msgBody = msgBody;
        this.image = image;
    }
    
    @Override
    public void run() {
        /*Session s = HiberUtil.getSessionFactory().openSession();
        String fcm_server_key = ((ApplicationSetting) s.load(ApplicationSetting.class, "fcm_server_key")).getValue();
        String base_url = ((ApplicationSetting) s.load(ApplicationSetting.class, "base_url")).getValue();
        s.close();

        if (image == null) {
            image = "";
        } else {
            image = ",\r\n     \"image\": \"" + base_url + image + "\"\r\n";
        }

        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\r\n \"to\" : \"" + to + "\",\r\n \"notification\" : {\r\n     \"body\" : \"" + msgBody + "\",\r\n     \"title\": \"" + title + "\",\r\n     \"sound\":\"default\",\r\n     \"priority\":\"high\" " + image + " }\r\n}");
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key=" + fcm_server_key)
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}

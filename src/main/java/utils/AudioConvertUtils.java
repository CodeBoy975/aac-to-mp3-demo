package utils;

import ws.schild.jave.AudioAttributes;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * @author pengjie_yao
 * @date 2019年12月23日11:35:20
 */
public class AudioConvertUtils {

    /**
     * 根据远程资源路径，下载资源到本地临时目录
     *
     * @param remoteSourceUrl 远程资源路径
     * @param tmpFileFolder   本地临时目录
     * @return 下载后的文件物理路径
     */
    private static String downloadSource(String remoteSourceUrl, String tmpFileFolder) throws Exception {
        //下载资源
        URL url = new URL(remoteSourceUrl);
        DataInputStream dataInputStream = new DataInputStream(url.openStream());
        String tmpFilePath = tmpFileFolder + getOriginalFileName(remoteSourceUrl);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(tmpFilePath));
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = dataInputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, length);
            // System.out.println("下载中....");
        }
        // System.out.println("下载完成...");
        dataInputStream.close();
        fileOutputStream.close();

        return tmpFilePath;
    }

    /**
     * 将本地音频文件转换成mp3格式文件
     *
     * @param localFilePath 本地音频文件物理路径
     * @param targetPath    转换后mp3文件的物理路径
     */
    public static void changeLocalSourceToMp3(String localFilePath, String targetPath) throws Exception {

        File source = new File(localFilePath);
        MultimediaObject multimediaObject = new MultimediaObject(source);
        File target = new File(targetPath);
        AudioAttributes audio = new AudioAttributes();
        Encoder encoder = new Encoder();

        audio.setCodec("libmp3lame");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);

        encoder.encode(multimediaObject, target, attrs);
    }

    /**
     * 下载远程文件到本地，然后转换成mp3格式，并返回mp3的文件绝对路径
     * <p>每次会优先检测mp3文件是否已存在，存在则直接返回mp3绝对路径，否则下载然后转换成mp3再返回</p>
     *
     * @param remoteSourceUrl 远程文件的URL
     * @param tmpFolder       临时文件存放的目录，如/usr/consult/tmp/
     * @return 转换成mp3的文件的绝对路径，如/usr/consult/tmp/fcd124a2-ed6c-4407-b574-81ecc51b4eb4.mp3
     */
    public static String changeRemoteSourceToMp3(String remoteSourceUrl, String tmpFolder) throws Exception {
        // 检测该文件是否已经下载并转换过一次，如果是，则直接返回
        String remoteSourceNameWithoutSuffix = getNameWithoutSuffix(remoteSourceUrl);
        // 格式为/usr/consult/tmp/fcd124a2-ed6c-4407-b574-81ecc51b4eb4.mp3
        String mp3FilePath = tmpFolder + File.separator + remoteSourceNameWithoutSuffix + ".mp3";
        File audioFile = new File(mp3FilePath);
        if (audioFile.exists()) {
            // 文件已在之前转换过一次，直接返回即可
            return mp3FilePath;
        }

        // 下载资源到临时目录
        String tmpRemoteFilePath = downloadSource(remoteSourceUrl, tmpFolder);
        // 转换成mp3格式
        changeLocalSourceToMp3(tmpRemoteFilePath, mp3FilePath);

        return mp3FilePath;
    }

    /**
     * 根据文件url获取文件名（包含后缀名）
     *
     * @param url 文件url
     * @return 文件名（包含后缀名）
     */
    private static String getOriginalFileName(String url) {
        String[] sarry = url.split("/");
        return sarry[sarry.length - 1];
    }

    /**
     * 根据文件url获取文件名（不包含后缀名）
     *
     * @param url 文件url
     * @return 文件名（包含后缀名）
     */
    private static String getNameWithoutSuffix(String url) {
        String originalFileName = getOriginalFileName(url);
        return originalFileName.substring(0, originalFileName.indexOf("."));
    }

    public static void main(String[] args) throws Exception {

        String result = changeRemoteSourceToMp3(
                "https://pro-lecture.zaihujk.com/48bddf5b252211eaa5d7190866a11611.aac", "D:\\");
        System.out.println(result);
    }

}
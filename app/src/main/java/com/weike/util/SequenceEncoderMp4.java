package com.weike.util;//package com.example.demo_surfaceview.util;
//
//
//import android.graphics.ColorSpace;
//import android.graphics.Picture;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.channels.SeekableByteChannel;
//import java.util.ArrayList;
//
///**
// * projectName: 	    Jcodec2
// * packageName:	        com.luoxiang.org.jcodec
// * className:	        SequenceEncoderMp4
// * author:	            Luoxiang
// * time:	            2017/1/6	17:40
// * desc:	            TODO
// *
// * svnVersion:	        $Rev
// * upDateAuthor:	    Vincent
// * upDate:	            2017/1/6
// * upDateDesc:	        TODO
// */
//
//public class SequenceEncoderMp4 extends org.jcodec.api.android.SequenceEncoder {
//
//    /**
//     * 控制帧率缩放,时间长度调整这个值
//     * 值越小,生成视频时间越长
//     * 如:timeScale = 50     一秒视屏采用50张图片
//     */
//    private int timeScale = 50;
//
//
//    public SequenceEncoderMp4(File out)
//            throws IOException
//    {
//        super(out);
//        this.ch = NIOUtils.writableFileChannel(out);
//
//        // Muxer that will store the encoded frames
//        muxer = new MP4Muxer(ch, Brand.MP4);
//
//        // Add video track to muxer
//        outTrack = muxer.addTrack(TrackType.VIDEO, timeScale);
//
//        // Allocate a buffer big enough to hold output frames
//        _out = ByteBuffer.allocate(1920 * 1080 * 6);
//
//        // Create an instance of encoder
//        encoder = new H264Encoder();
//
//        // Transform to convert between RGB and YUV
//        transform = ColorUtil.getTransform(ColorSpace.RGB, encoder.getSupportedColorSpaces()[0]);
//
//        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
//        // MP4
//        spsList = new ArrayList<ByteBuffer>();
//        ppsList = new ArrayList<ByteBuffer>();
//    }
//
//    private SeekableByteChannel   ch;
//    private Picture               toEncode;
//    private Transform             transform;
//    private H264Encoder           encoder;
//    private ArrayList<ByteBuffer> spsList;
//    private ArrayList<ByteBuffer> ppsList;
//    private FramesMP4MuxerTrack   outTrack;
//    private ByteBuffer            _out;
//    private int                   frameNo;
//    private MP4Muxer              muxer;
//
//
//    public void encodeNativeFrame(Picture pic) throws IOException {
//        if (toEncode == null) {
//            toEncode = Picture.create(pic.getWidth() , pic.getHeight() , encoder.getSupportedColorSpaces()[0]);
//        }
//
//        // Perform conversion
//        transform.transform(pic, toEncode);
//
//        // Encode image into H.264 frame, the result is stored in '_out' buffer
//        _out.clear();
//        ByteBuffer result = encoder.encodeFrame(toEncode, _out);
//
//        // Based on the frame above form correct MP4 packet
//        spsList.clear();
//        ppsList.clear();
//        H264Utils.wipePS(result, spsList, ppsList);
//        H264Utils.encodeMOVPacket(result);
//
//        // Add packet to video track
//        outTrack.addFrame(new MP4Packet(result, frameNo, timeScale, 1, frameNo, true, null, frameNo, 0));
//
//        frameNo++;
//    }
//
//    public void finish() throws IOException {
//        // Push saved SPS/PPS to a special storage in MP4
//        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList, 4));
//
//        // Write MP4 header and finalize recording
//        muxer.writeHeader();
//        NIOUtils.closeQuietly(ch);
//    }
//}

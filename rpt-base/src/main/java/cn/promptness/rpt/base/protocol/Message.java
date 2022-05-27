package cn.promptness.rpt.base.protocol;

/**
 * 客户端-服务器自定义通信协议
 */
public class Message {

    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 元数据
     */
    private Meta meta;

    /**
     * 消息内容
     */
    private byte[] data;

    public Message() {

    }

    public Message(MessageType type, Meta meta, byte[] data) {
        this.meta = meta;
        this.data = data;
        this.type = type;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

}

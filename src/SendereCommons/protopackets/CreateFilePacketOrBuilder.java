// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package SendereCommons.protopackets;

public interface CreateFilePacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:CreateFilePacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 transmissionId = 1;</code>
   * @return The transmissionId.
   */
  long getTransmissionId();

  /**
   * <code>string fileName = 2;</code>
   * @return The fileName.
   */
  java.lang.String getFileName();
  /**
   * <code>string fileName = 2;</code>
   * @return The bytes for fileName.
   */
  com.google.protobuf.ByteString
      getFileNameBytes();
}

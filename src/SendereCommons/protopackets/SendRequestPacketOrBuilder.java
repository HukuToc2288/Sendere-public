// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package SendereCommons.protopackets;

public interface SendRequestPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:SendRequestPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 transmissionId = 1;</code>
   * @return The transmissionId.
   */
  long getTransmissionId();

  /**
   * <code>bool isDirectory = 2;</code>
   * @return The isDirectory.
   */
  boolean getIsDirectory();

  /**
   * <code>string fileName = 3;</code>
   * @return The fileName.
   */
  java.lang.String getFileName();
  /**
   * <code>string fileName = 3;</code>
   * @return The bytes for fileName.
   */
  com.google.protobuf.ByteString
      getFileNameBytes();
}

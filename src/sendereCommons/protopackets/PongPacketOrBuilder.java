// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package sendereCommons.protopackets;

public interface PongPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:PongPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 suid = 1;</code>
   * @return The suid.
   */
  long getSuid();

  /**
   * <code>string nickname = 2;</code>
   * @return The nickname.
   */
  java.lang.String getNickname();
  /**
   * <code>string nickname = 2;</code>
   * @return The bytes for nickname.
   */
  com.google.protobuf.ByteString
      getNicknameBytes();

  /**
   * <code>bool supportGzip = 3;</code>
   * @return The supportGzip.
   */
  boolean getSupportGzip();

  /**
   * <code>bool supportEncryption = 4;</code>
   * @return The supportEncryption.
   */
  boolean getSupportEncryption();
}
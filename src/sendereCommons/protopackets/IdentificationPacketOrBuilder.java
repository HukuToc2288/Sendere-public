// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package sendereCommons.protopackets;

public interface IdentificationPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:IdentificationPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string nickname = 1;</code>
   * @return The nickname.
   */
  java.lang.String getNickname();
  /**
   * <code>string nickname = 1;</code>
   * @return The bytes for nickname.
   */
  com.google.protobuf.ByteString
      getNicknameBytes();

  /**
   * <code>bool remoteIdentified = 2;</code>
   * @return The remoteIdentified.
   */
  boolean getRemoteIdentified();

  /**
   * <code>bool supprotEnctryption = 3;</code>
   * @return The supprotEnctryption.
   */
  boolean getSupprotEnctryption();
}

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package sendereCommons.protopackets;

public interface EncryptionCheckPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:EncryptionCheckPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bytes encrypted = 1;</code>
   * @return The encrypted.
   */
  com.google.protobuf.ByteString getEncrypted();

  /**
   * <code>bytes cleartext = 2;</code>
   * @return The cleartext.
   */
  com.google.protobuf.ByteString getCleartext();

  /**
   * <code>bool senderTrusts = 3;</code>
   * @return The senderTrusts.
   */
  boolean getSenderTrusts();
}
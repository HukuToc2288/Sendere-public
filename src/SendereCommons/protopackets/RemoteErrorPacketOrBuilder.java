// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package SendereCommons.protopackets;

public interface RemoteErrorPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:RemoteErrorPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.RemoteErrorPacket.ErrorType errorType = 1;</code>
   * @return The enum numeric value on the wire for errorType.
   */
  int getErrorTypeValue();
  /**
   * <code>.RemoteErrorPacket.ErrorType errorType = 1;</code>
   * @return The errorType.
   */
  SendereCommons.protopackets.RemoteErrorPacket.ErrorType getErrorType();

  /**
   * <code>string extraMessage = 2;</code>
   * @return Whether the extraMessage field is set.
   */
  boolean hasExtraMessage();
  /**
   * <code>string extraMessage = 2;</code>
   * @return The extraMessage.
   */
  java.lang.String getExtraMessage();
  /**
   * <code>string extraMessage = 2;</code>
   * @return The bytes for extraMessage.
   */
  com.google.protobuf.ByteString
      getExtraMessageBytes();
}

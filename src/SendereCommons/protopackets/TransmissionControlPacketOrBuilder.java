// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package SendereCommons.protopackets;

public interface TransmissionControlPacketOrBuilder extends
    // @@protoc_insertion_point(interface_extends:TransmissionControlPacket)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 transmissionId = 1;</code>
   * @return The transmissionId.
   */
  long getTransmissionId();

  /**
   * <code>.TransmissionControlPacket.Signal signal = 2;</code>
   * @return The enum numeric value on the wire for signal.
   */
  int getSignalValue();
  /**
   * <code>.TransmissionControlPacket.Signal signal = 2;</code>
   * @return The signal.
   */
  SendereCommons.protopackets.TransmissionControlPacket.Signal getSignal();
}

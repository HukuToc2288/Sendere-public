// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package SendereCommons.protopackets;

/**
 * Protobuf type {@code SendResponsePacket}
 */
public final class SendResponsePacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:SendResponsePacket)
    SendResponsePacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SendResponsePacket.newBuilder() to construct.
  private SendResponsePacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SendResponsePacket() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new SendResponsePacket();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private SendResponsePacket(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            transmissionId_ = input.readInt64();
            break;
          }
          case 16: {

            accepted_ = input.readBool();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return SendereCommons.protopackets.PacketProtos.internal_static_SendResponsePacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return SendereCommons.protopackets.PacketProtos.internal_static_SendResponsePacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            SendereCommons.protopackets.SendResponsePacket.class, SendereCommons.protopackets.SendResponsePacket.Builder.class);
  }

  public static final int TRANSMISSIONID_FIELD_NUMBER = 1;
  private long transmissionId_;
  /**
   * <code>int64 transmissionId = 1;</code>
   * @return The transmissionId.
   */
  @java.lang.Override
  public long getTransmissionId() {
    return transmissionId_;
  }

  public static final int ACCEPTED_FIELD_NUMBER = 2;
  private boolean accepted_;
  /**
   * <code>bool accepted = 2;</code>
   * @return The accepted.
   */
  @java.lang.Override
  public boolean getAccepted() {
    return accepted_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (transmissionId_ != 0L) {
      output.writeInt64(1, transmissionId_);
    }
    if (accepted_ != false) {
      output.writeBool(2, accepted_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (transmissionId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, transmissionId_);
    }
    if (accepted_ != false) {
      size += com.google.protobuf.CodedOutputStream
        .computeBoolSize(2, accepted_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof SendereCommons.protopackets.SendResponsePacket)) {
      return super.equals(obj);
    }
    SendereCommons.protopackets.SendResponsePacket other = (SendereCommons.protopackets.SendResponsePacket) obj;

    if (getTransmissionId()
        != other.getTransmissionId()) return false;
    if (getAccepted()
        != other.getAccepted()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + TRANSMISSIONID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getTransmissionId());
    hash = (37 * hash) + ACCEPTED_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
        getAccepted());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static SendereCommons.protopackets.SendResponsePacket parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(SendereCommons.protopackets.SendResponsePacket prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code SendResponsePacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:SendResponsePacket)
      SendereCommons.protopackets.SendResponsePacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return SendereCommons.protopackets.PacketProtos.internal_static_SendResponsePacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return SendereCommons.protopackets.PacketProtos.internal_static_SendResponsePacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              SendereCommons.protopackets.SendResponsePacket.class, SendereCommons.protopackets.SendResponsePacket.Builder.class);
    }

    // Construct using SendereCommons.protopackets.SendResponsePacket.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      transmissionId_ = 0L;

      accepted_ = false;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return SendereCommons.protopackets.PacketProtos.internal_static_SendResponsePacket_descriptor;
    }

    @java.lang.Override
    public SendereCommons.protopackets.SendResponsePacket getDefaultInstanceForType() {
      return SendereCommons.protopackets.SendResponsePacket.getDefaultInstance();
    }

    @java.lang.Override
    public SendereCommons.protopackets.SendResponsePacket build() {
      SendereCommons.protopackets.SendResponsePacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public SendereCommons.protopackets.SendResponsePacket buildPartial() {
      SendereCommons.protopackets.SendResponsePacket result = new SendereCommons.protopackets.SendResponsePacket(this);
      result.transmissionId_ = transmissionId_;
      result.accepted_ = accepted_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof SendereCommons.protopackets.SendResponsePacket) {
        return mergeFrom((SendereCommons.protopackets.SendResponsePacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(SendereCommons.protopackets.SendResponsePacket other) {
      if (other == SendereCommons.protopackets.SendResponsePacket.getDefaultInstance()) return this;
      if (other.getTransmissionId() != 0L) {
        setTransmissionId(other.getTransmissionId());
      }
      if (other.getAccepted() != false) {
        setAccepted(other.getAccepted());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      SendereCommons.protopackets.SendResponsePacket parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (SendereCommons.protopackets.SendResponsePacket) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private long transmissionId_ ;
    /**
     * <code>int64 transmissionId = 1;</code>
     * @return The transmissionId.
     */
    @java.lang.Override
    public long getTransmissionId() {
      return transmissionId_;
    }
    /**
     * <code>int64 transmissionId = 1;</code>
     * @param value The transmissionId to set.
     * @return This builder for chaining.
     */
    public Builder setTransmissionId(long value) {
      
      transmissionId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 transmissionId = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearTransmissionId() {
      
      transmissionId_ = 0L;
      onChanged();
      return this;
    }

    private boolean accepted_ ;
    /**
     * <code>bool accepted = 2;</code>
     * @return The accepted.
     */
    @java.lang.Override
    public boolean getAccepted() {
      return accepted_;
    }
    /**
     * <code>bool accepted = 2;</code>
     * @param value The accepted to set.
     * @return This builder for chaining.
     */
    public Builder setAccepted(boolean value) {
      
      accepted_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bool accepted = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearAccepted() {
      
      accepted_ = false;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:SendResponsePacket)
  }

  // @@protoc_insertion_point(class_scope:SendResponsePacket)
  private static final SendereCommons.protopackets.SendResponsePacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new SendereCommons.protopackets.SendResponsePacket();
  }

  public static SendereCommons.protopackets.SendResponsePacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SendResponsePacket>
      PARSER = new com.google.protobuf.AbstractParser<SendResponsePacket>() {
    @java.lang.Override
    public SendResponsePacket parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new SendResponsePacket(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SendResponsePacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SendResponsePacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public SendereCommons.protopackets.SendResponsePacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


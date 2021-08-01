// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package sendereCommons.protopackets;

/**
 * Protobuf type {@code CloseFilePacket}
 */
public final class CloseFilePacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:CloseFilePacket)
    CloseFilePacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use CloseFilePacket.newBuilder() to construct.
  private CloseFilePacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private CloseFilePacket() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new CloseFilePacket();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private CloseFilePacket(
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
    return sendereCommons.protopackets.PacketProtos.internal_static_CloseFilePacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return sendereCommons.protopackets.PacketProtos.internal_static_CloseFilePacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            sendereCommons.protopackets.CloseFilePacket.class, sendereCommons.protopackets.CloseFilePacket.Builder.class);
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
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof sendereCommons.protopackets.CloseFilePacket)) {
      return super.equals(obj);
    }
    sendereCommons.protopackets.CloseFilePacket other = (sendereCommons.protopackets.CloseFilePacket) obj;

    if (getTransmissionId()
        != other.getTransmissionId()) return false;
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
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.CloseFilePacket parseFrom(
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
  public static Builder newBuilder(sendereCommons.protopackets.CloseFilePacket prototype) {
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
   * Protobuf type {@code CloseFilePacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:CloseFilePacket)
      sendereCommons.protopackets.CloseFilePacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return sendereCommons.protopackets.PacketProtos.internal_static_CloseFilePacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return sendereCommons.protopackets.PacketProtos.internal_static_CloseFilePacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              sendereCommons.protopackets.CloseFilePacket.class, sendereCommons.protopackets.CloseFilePacket.Builder.class);
    }

    // Construct using sendereCommons.protopackets.CloseFilePacket.newBuilder()
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

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return sendereCommons.protopackets.PacketProtos.internal_static_CloseFilePacket_descriptor;
    }

    @java.lang.Override
    public sendereCommons.protopackets.CloseFilePacket getDefaultInstanceForType() {
      return sendereCommons.protopackets.CloseFilePacket.getDefaultInstance();
    }

    @java.lang.Override
    public sendereCommons.protopackets.CloseFilePacket build() {
      sendereCommons.protopackets.CloseFilePacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public sendereCommons.protopackets.CloseFilePacket buildPartial() {
      sendereCommons.protopackets.CloseFilePacket result = new sendereCommons.protopackets.CloseFilePacket(this);
      result.transmissionId_ = transmissionId_;
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
      if (other instanceof sendereCommons.protopackets.CloseFilePacket) {
        return mergeFrom((sendereCommons.protopackets.CloseFilePacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(sendereCommons.protopackets.CloseFilePacket other) {
      if (other == sendereCommons.protopackets.CloseFilePacket.getDefaultInstance()) return this;
      if (other.getTransmissionId() != 0L) {
        setTransmissionId(other.getTransmissionId());
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
      sendereCommons.protopackets.CloseFilePacket parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (sendereCommons.protopackets.CloseFilePacket) e.getUnfinishedMessage();
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


    // @@protoc_insertion_point(builder_scope:CloseFilePacket)
  }

  // @@protoc_insertion_point(class_scope:CloseFilePacket)
  private static final sendereCommons.protopackets.CloseFilePacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new sendereCommons.protopackets.CloseFilePacket();
  }

  public static sendereCommons.protopackets.CloseFilePacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<CloseFilePacket>
      PARSER = new com.google.protobuf.AbstractParser<CloseFilePacket>() {
    @java.lang.Override
    public CloseFilePacket parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new CloseFilePacket(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<CloseFilePacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<CloseFilePacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public sendereCommons.protopackets.CloseFilePacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

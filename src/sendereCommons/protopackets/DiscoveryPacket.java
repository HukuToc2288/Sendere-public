// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package sendereCommons.protopackets;

/**
 * Protobuf type {@code DiscoveryPacket}
 */
public final class DiscoveryPacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:DiscoveryPacket)
    DiscoveryPacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use DiscoveryPacket.newBuilder() to construct.
  private DiscoveryPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private DiscoveryPacket() {
    address_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new DiscoveryPacket();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private DiscoveryPacket(
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

            suid_ = input.readInt64();
            break;
          }
          case 18: {

            address_ = input.readBytes();
            break;
          }
          case 24: {

            port_ = input.readInt32();
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
    return sendereCommons.protopackets.PacketProtos.internal_static_DiscoveryPacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return sendereCommons.protopackets.PacketProtos.internal_static_DiscoveryPacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            sendereCommons.protopackets.DiscoveryPacket.class, sendereCommons.protopackets.DiscoveryPacket.Builder.class);
  }

  public static final int SUID_FIELD_NUMBER = 1;
  private long suid_;
  /**
   * <code>int64 suid = 1;</code>
   * @return The suid.
   */
  @java.lang.Override
  public long getSuid() {
    return suid_;
  }

  public static final int ADDRESS_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString address_;
  /**
   * <code>bytes address = 2;</code>
   * @return The address.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getAddress() {
    return address_;
  }

  public static final int PORT_FIELD_NUMBER = 3;
  private int port_;
  /**
   * <code>int32 port = 3;</code>
   * @return The port.
   */
  @java.lang.Override
  public int getPort() {
    return port_;
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
    if (suid_ != 0L) {
      output.writeInt64(1, suid_);
    }
    if (!address_.isEmpty()) {
      output.writeBytes(2, address_);
    }
    if (port_ != 0) {
      output.writeInt32(3, port_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (suid_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, suid_);
    }
    if (!address_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, address_);
    }
    if (port_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, port_);
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
    if (!(obj instanceof sendereCommons.protopackets.DiscoveryPacket)) {
      return super.equals(obj);
    }
    sendereCommons.protopackets.DiscoveryPacket other = (sendereCommons.protopackets.DiscoveryPacket) obj;

    if (getSuid()
        != other.getSuid()) return false;
    if (!getAddress()
        .equals(other.getAddress())) return false;
    if (getPort()
        != other.getPort()) return false;
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
    hash = (37 * hash) + SUID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSuid());
    hash = (37 * hash) + ADDRESS_FIELD_NUMBER;
    hash = (53 * hash) + getAddress().hashCode();
    hash = (37 * hash) + PORT_FIELD_NUMBER;
    hash = (53 * hash) + getPort();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.DiscoveryPacket parseFrom(
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
  public static Builder newBuilder(sendereCommons.protopackets.DiscoveryPacket prototype) {
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
   * Protobuf type {@code DiscoveryPacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:DiscoveryPacket)
      sendereCommons.protopackets.DiscoveryPacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return sendereCommons.protopackets.PacketProtos.internal_static_DiscoveryPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return sendereCommons.protopackets.PacketProtos.internal_static_DiscoveryPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              sendereCommons.protopackets.DiscoveryPacket.class, sendereCommons.protopackets.DiscoveryPacket.Builder.class);
    }

    // Construct using sendereCommons.protopackets.DiscoveryPacket.newBuilder()
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
      suid_ = 0L;

      address_ = com.google.protobuf.ByteString.EMPTY;

      port_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return sendereCommons.protopackets.PacketProtos.internal_static_DiscoveryPacket_descriptor;
    }

    @java.lang.Override
    public sendereCommons.protopackets.DiscoveryPacket getDefaultInstanceForType() {
      return sendereCommons.protopackets.DiscoveryPacket.getDefaultInstance();
    }

    @java.lang.Override
    public sendereCommons.protopackets.DiscoveryPacket build() {
      sendereCommons.protopackets.DiscoveryPacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public sendereCommons.protopackets.DiscoveryPacket buildPartial() {
      sendereCommons.protopackets.DiscoveryPacket result = new sendereCommons.protopackets.DiscoveryPacket(this);
      result.suid_ = suid_;
      result.address_ = address_;
      result.port_ = port_;
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
      if (other instanceof sendereCommons.protopackets.DiscoveryPacket) {
        return mergeFrom((sendereCommons.protopackets.DiscoveryPacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(sendereCommons.protopackets.DiscoveryPacket other) {
      if (other == sendereCommons.protopackets.DiscoveryPacket.getDefaultInstance()) return this;
      if (other.getSuid() != 0L) {
        setSuid(other.getSuid());
      }
      if (other.getAddress() != com.google.protobuf.ByteString.EMPTY) {
        setAddress(other.getAddress());
      }
      if (other.getPort() != 0) {
        setPort(other.getPort());
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
      sendereCommons.protopackets.DiscoveryPacket parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (sendereCommons.protopackets.DiscoveryPacket) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private long suid_ ;
    /**
     * <code>int64 suid = 1;</code>
     * @return The suid.
     */
    @java.lang.Override
    public long getSuid() {
      return suid_;
    }
    /**
     * <code>int64 suid = 1;</code>
     * @param value The suid to set.
     * @return This builder for chaining.
     */
    public Builder setSuid(long value) {
      
      suid_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 suid = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearSuid() {
      
      suid_ = 0L;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString address_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes address = 2;</code>
     * @return The address.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getAddress() {
      return address_;
    }
    /**
     * <code>bytes address = 2;</code>
     * @param value The address to set.
     * @return This builder for chaining.
     */
    public Builder setAddress(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      address_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes address = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearAddress() {
      
      address_ = getDefaultInstance().getAddress();
      onChanged();
      return this;
    }

    private int port_ ;
    /**
     * <code>int32 port = 3;</code>
     * @return The port.
     */
    @java.lang.Override
    public int getPort() {
      return port_;
    }
    /**
     * <code>int32 port = 3;</code>
     * @param value The port to set.
     * @return This builder for chaining.
     */
    public Builder setPort(int value) {
      
      port_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 port = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearPort() {
      
      port_ = 0;
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


    // @@protoc_insertion_point(builder_scope:DiscoveryPacket)
  }

  // @@protoc_insertion_point(class_scope:DiscoveryPacket)
  private static final sendereCommons.protopackets.DiscoveryPacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new sendereCommons.protopackets.DiscoveryPacket();
  }

  public static sendereCommons.protopackets.DiscoveryPacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<DiscoveryPacket>
      PARSER = new com.google.protobuf.AbstractParser<DiscoveryPacket>() {
    @java.lang.Override
    public DiscoveryPacket parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new DiscoveryPacket(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<DiscoveryPacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<DiscoveryPacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public sendereCommons.protopackets.DiscoveryPacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}


// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: ping-packet.proto

package sendereCommons.protopackets;

/**
 * Protobuf type {@code SignedPacket}
 */
public final class SignedPacket extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:SignedPacket)
    SignedPacketOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SignedPacket.newBuilder() to construct.
  private SignedPacket(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SignedPacket() {
    nestedPacketBytes_ = com.google.protobuf.ByteString.EMPTY;
    salt_ = com.google.protobuf.ByteString.EMPTY;
    signature_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new SignedPacket();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private SignedPacket(
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
          case 10: {

            nestedPacketBytes_ = input.readBytes();
            break;
          }
          case 18: {

            salt_ = input.readBytes();
            break;
          }
          case 26: {

            signature_ = input.readBytes();
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
    return sendereCommons.protopackets.PacketProtos.internal_static_SignedPacket_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return sendereCommons.protopackets.PacketProtos.internal_static_SignedPacket_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            sendereCommons.protopackets.SignedPacket.class, sendereCommons.protopackets.SignedPacket.Builder.class);
  }

  public static final int NESTEDPACKETBYTES_FIELD_NUMBER = 1;
  private com.google.protobuf.ByteString nestedPacketBytes_;
  /**
   * <code>bytes nestedPacketBytes = 1;</code>
   * @return The nestedPacketBytes.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getNestedPacketBytes() {
    return nestedPacketBytes_;
  }

  public static final int SALT_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString salt_;
  /**
   * <code>bytes salt = 2;</code>
   * @return The salt.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getSalt() {
    return salt_;
  }

  public static final int SIGNATURE_FIELD_NUMBER = 3;
  private com.google.protobuf.ByteString signature_;
  /**
   * <code>bytes signature = 3;</code>
   * @return The signature.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getSignature() {
    return signature_;
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
    if (!nestedPacketBytes_.isEmpty()) {
      output.writeBytes(1, nestedPacketBytes_);
    }
    if (!salt_.isEmpty()) {
      output.writeBytes(2, salt_);
    }
    if (!signature_.isEmpty()) {
      output.writeBytes(3, signature_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!nestedPacketBytes_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(1, nestedPacketBytes_);
    }
    if (!salt_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, salt_);
    }
    if (!signature_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(3, signature_);
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
    if (!(obj instanceof sendereCommons.protopackets.SignedPacket)) {
      return super.equals(obj);
    }
    sendereCommons.protopackets.SignedPacket other = (sendereCommons.protopackets.SignedPacket) obj;

    if (!getNestedPacketBytes()
        .equals(other.getNestedPacketBytes())) return false;
    if (!getSalt()
        .equals(other.getSalt())) return false;
    if (!getSignature()
        .equals(other.getSignature())) return false;
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
    hash = (37 * hash) + NESTEDPACKETBYTES_FIELD_NUMBER;
    hash = (53 * hash) + getNestedPacketBytes().hashCode();
    hash = (37 * hash) + SALT_FIELD_NUMBER;
    hash = (53 * hash) + getSalt().hashCode();
    hash = (37 * hash) + SIGNATURE_FIELD_NUMBER;
    hash = (53 * hash) + getSignature().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static sendereCommons.protopackets.SignedPacket parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static sendereCommons.protopackets.SignedPacket parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.SignedPacket parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static sendereCommons.protopackets.SignedPacket parseFrom(
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
  public static Builder newBuilder(sendereCommons.protopackets.SignedPacket prototype) {
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
   * Protobuf type {@code SignedPacket}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:SignedPacket)
      sendereCommons.protopackets.SignedPacketOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return sendereCommons.protopackets.PacketProtos.internal_static_SignedPacket_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return sendereCommons.protopackets.PacketProtos.internal_static_SignedPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              sendereCommons.protopackets.SignedPacket.class, sendereCommons.protopackets.SignedPacket.Builder.class);
    }

    // Construct using sendereCommons.protopackets.SignedPacket.newBuilder()
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
      nestedPacketBytes_ = com.google.protobuf.ByteString.EMPTY;

      salt_ = com.google.protobuf.ByteString.EMPTY;

      signature_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return sendereCommons.protopackets.PacketProtos.internal_static_SignedPacket_descriptor;
    }

    @java.lang.Override
    public sendereCommons.protopackets.SignedPacket getDefaultInstanceForType() {
      return sendereCommons.protopackets.SignedPacket.getDefaultInstance();
    }

    @java.lang.Override
    public sendereCommons.protopackets.SignedPacket build() {
      sendereCommons.protopackets.SignedPacket result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public sendereCommons.protopackets.SignedPacket buildPartial() {
      sendereCommons.protopackets.SignedPacket result = new sendereCommons.protopackets.SignedPacket(this);
      result.nestedPacketBytes_ = nestedPacketBytes_;
      result.salt_ = salt_;
      result.signature_ = signature_;
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
      if (other instanceof sendereCommons.protopackets.SignedPacket) {
        return mergeFrom((sendereCommons.protopackets.SignedPacket)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(sendereCommons.protopackets.SignedPacket other) {
      if (other == sendereCommons.protopackets.SignedPacket.getDefaultInstance()) return this;
      if (other.getNestedPacketBytes() != com.google.protobuf.ByteString.EMPTY) {
        setNestedPacketBytes(other.getNestedPacketBytes());
      }
      if (other.getSalt() != com.google.protobuf.ByteString.EMPTY) {
        setSalt(other.getSalt());
      }
      if (other.getSignature() != com.google.protobuf.ByteString.EMPTY) {
        setSignature(other.getSignature());
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
      sendereCommons.protopackets.SignedPacket parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (sendereCommons.protopackets.SignedPacket) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.google.protobuf.ByteString nestedPacketBytes_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes nestedPacketBytes = 1;</code>
     * @return The nestedPacketBytes.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getNestedPacketBytes() {
      return nestedPacketBytes_;
    }
    /**
     * <code>bytes nestedPacketBytes = 1;</code>
     * @param value The nestedPacketBytes to set.
     * @return This builder for chaining.
     */
    public Builder setNestedPacketBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      nestedPacketBytes_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes nestedPacketBytes = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearNestedPacketBytes() {
      
      nestedPacketBytes_ = getDefaultInstance().getNestedPacketBytes();
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString salt_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes salt = 2;</code>
     * @return The salt.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getSalt() {
      return salt_;
    }
    /**
     * <code>bytes salt = 2;</code>
     * @param value The salt to set.
     * @return This builder for chaining.
     */
    public Builder setSalt(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      salt_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes salt = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearSalt() {
      
      salt_ = getDefaultInstance().getSalt();
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString signature_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes signature = 3;</code>
     * @return The signature.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getSignature() {
      return signature_;
    }
    /**
     * <code>bytes signature = 3;</code>
     * @param value The signature to set.
     * @return This builder for chaining.
     */
    public Builder setSignature(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      signature_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes signature = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearSignature() {
      
      signature_ = getDefaultInstance().getSignature();
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


    // @@protoc_insertion_point(builder_scope:SignedPacket)
  }

  // @@protoc_insertion_point(class_scope:SignedPacket)
  private static final sendereCommons.protopackets.SignedPacket DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new sendereCommons.protopackets.SignedPacket();
  }

  public static sendereCommons.protopackets.SignedPacket getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SignedPacket>
      PARSER = new com.google.protobuf.AbstractParser<SignedPacket>() {
    @java.lang.Override
    public SignedPacket parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new SignedPacket(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SignedPacket> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SignedPacket> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public sendereCommons.protopackets.SignedPacket getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

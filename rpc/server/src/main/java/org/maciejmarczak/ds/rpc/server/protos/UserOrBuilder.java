// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: user.proto

package org.maciejmarczak.ds.rpc.server.protos;

public interface UserOrBuilder extends
    // @@protoc_insertion_point(interface_extends:User)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string id = 1;</code>
   */
  java.lang.String getId();
  /**
   * <code>string id = 1;</code>
   */
  com.google.protobuf.ByteString
      getIdBytes();

  /**
   * <code>string full_name = 2;</code>
   */
  java.lang.String getFullName();
  /**
   * <code>string full_name = 2;</code>
   */
  com.google.protobuf.ByteString
      getFullNameBytes();

  /**
   * <code>.User.Role role = 3;</code>
   */
  int getRoleValue();
  /**
   * <code>.User.Role role = 3;</code>
   */
  org.maciejmarczak.ds.rpc.server.protos.User.Role getRole();

  /**
   * <code>.Contact contact = 4;</code>
   */
  boolean hasContact();
  /**
   * <code>.Contact contact = 4;</code>
   */
  org.maciejmarczak.ds.rpc.server.protos.Contact getContact();
  /**
   * <code>.Contact contact = 4;</code>
   */
  org.maciejmarczak.ds.rpc.server.protos.ContactOrBuilder getContactOrBuilder();
}

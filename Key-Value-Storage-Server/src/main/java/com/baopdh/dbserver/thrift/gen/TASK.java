/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.baopdh.dbserver.thrift.gen;


@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-04-15")
public enum TASK implements org.apache.thrift.TEnum {
  PUT(0),
  DELETE(1),
  WARNING(2);

  private final int value;

  private TASK(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  @org.apache.thrift.annotation.Nullable
  public static TASK findByValue(int value) { 
    switch (value) {
      case 0:
        return PUT;
      case 1:
        return DELETE;
      case 2:
        return WARNING;
      default:
        return null;
    }
  }
}

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\nookdevs\\nookdevs\\nookMarket\\src\\android\\content\\pm\\IPackageDeleteObserver.aidl
 */
package android.content.pm;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
/**
 * API for deletion callbacks from the Package Manager.
 *
 * {@hide}
 */
public interface IPackageDeleteObserver extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements android.content.pm.IPackageDeleteObserver
{
private static final java.lang.String DESCRIPTOR = "android.content.pm.IPackageDeleteObserver";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IPackageDeleteObserver interface,
 * generating a proxy if needed.
 */
public static android.content.pm.IPackageDeleteObserver asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof android.content.pm.IPackageDeleteObserver))) {
return ((android.content.pm.IPackageDeleteObserver)iin);
}
return new android.content.pm.IPackageDeleteObserver.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_packageDeleted:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.packageDeleted(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements android.content.pm.IPackageDeleteObserver
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void packageDeleted(boolean succeeded) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((succeeded)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_packageDeleted, _data, null, IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_packageDeleted = (IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void packageDeleted(boolean succeeded) throws android.os.RemoteException;
}

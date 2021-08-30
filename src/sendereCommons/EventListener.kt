package sendereCommons

import sendereCommons.protopackets.RemoteErrorPacket

open class EventListener {
    open fun onRemoteUserConnected(remoteUser: RemoteUser) {

    }

    open fun onRemoteUserFound(remoteUser: RemoteUser) {

    }

    open fun onRemoteUserUpdated(remoteUser: RemoteUser) {

    }

    open fun onTextMessageReceived(remoteUser: RemoteUser, message: String) {

    }

    open fun onSendRequestReceived(remoteUser: RemoteUser, request: InRequest) {

    }

    open fun onSendResponseReceived(remoteUser: RemoteUser, transmissionOut: TransmissionOut, allow: Boolean) {

    }

    open fun onRemoteUserDisconnected(remoteUser: RemoteUser) {

    }

    open fun onRemoteErrorReceived(remoteUser: RemoteUser, errorType: RemoteErrorPacket.ErrorType, extraMessage: String?) {

    }
}
package sendereCommons

object Settings {
    /**
     * It is, actually, your nickname, that will be displayed to other Sendere users
     */
    var nickname = "Sendere User"

    /**
     * This parameter determines should other people see this device in Sendere interface
     * Invisible device also shouldn't answer on any messages and send extra information
     * about itself in PING requests.
     * 0 - device completely invisible
     * 1 - device visible to all Sendere users
     * 2 - device visible for friends only (not implemented yet)
     */
    val visibility = 1

    /**
     * Directory where received files will be placed
     * Path has to be in UNIX style
     */
    val receivingDir = System.getProperty("user.home")

    /**
     * Show that user allows to receive text messages
     * If set to `true`, when packet with [Headers.TEXT] header will be received,
     * [Sendere.onTextMessageReceived] implementation will be invoked
     * Otherwise, nothing happens
     * Also when user sends text message when this parameter set to false, he should be
     * warned, that he won't be able to receive answer
     */
    val allowChat = true

    /**
     * Show that user allows to receive files and directories
     * If set to `true`, [Sendere.onSendRequest] will be invoked when user ready to answer it
     * Otherwise, send request will be automatically processed as denied
     * @see Sendere.processSendRequest
     */
    val allowReceiving = true

    // TODO make Sendere be able to detect another instances on computer
    // 29.06.2020 huku
    val allowMultiLaunch = true

    // TODO document it
    // 29.06.2020 huku

    // do not set it to "true" – compressing algorithm broken for now
    // 31.08.2021
    val allowGzip = false
}
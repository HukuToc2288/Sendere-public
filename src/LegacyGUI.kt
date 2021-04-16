import SendereCommons.*
import SendereCommons.protopackets.RemoteErrorPacket

import java.awt.*

import java.awt.event.ActionEvent

import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.HashMap
import javax.swing.*
import javax.swing.border.EmptyBorder

import javax.swing.table.DefaultTableModel


class LegacyGUI {

    lateinit var sendere: Sendere
    var previousSelection: JPanel? = null
    var usersEntries = HashMap<RemoteUser, JComponent>()

    fun init(){
        val mainWindow = JFrame()
        val tabbedPane = JTabbedPane()
        val compsToExperiment = GridLayoutDemo("Sendere")

        mainWindow.contentPane.layout = GridBagLayout()

        var constraints = GridBagConstraints()


        tabbedPane.addTab("Клиенты", compsToExperiment)

        tabbedPane.addTab("Передачи", JPanel())

        tabbedPane.addTab("Приём", JPanel())

        mainWindow.contentPane.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.weightx = 0.5
        constraints.weighty = 0.5

        mainWindow.contentPane.add(tabbedPane, constraints)

        constraints.gridx = 0
        constraints.gridy = 1
        constraints.fill = GridBagConstraints.BOTH

        //mainWindow.contentPane.add(contentPane, constraints)

        mainWindow.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainWindow.preferredSize = Dimension(800, 450)
        mainWindow.minimumSize = Dimension(301,300)
        mainWindow.pack()
        mainWindow.setLocationRelativeTo(null)
        mainWindow.isVisible = true

        NetworkList.updateList()
        sendere = object: Sendere(){
            override fun onRemoteErrorReceived(user: RemoteUser?, errorType: RemoteErrorPacket.ErrorType?, extraMessage: String?) {
                TODO("Not yet implemented")
            }

            override fun onRemoteUserUpdated(user: RemoteUser) {

            }

            override fun onRemoteUserConnected(user: RemoteUser) {
                println("user connected")
                val userPanel = createUserPanel(user)
                userPanel.background = UIManager.getColor("List.background")
                userPanel.isOpaque = true
                userPanel.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(mouseEvent: MouseEvent) {
                        super.mouseClicked(mouseEvent)
                        if (previousSelection != null)
                            previousSelection!!.background = UIManager.getColor("List.background")
                        previousSelection = userPanel
                        userPanel.background = UIManager.getColor("List.selectionBackground")
                        //userPanel.setOpaque(true);
                    }
                })
                compsToExperiment.add(userPanel)
                compsToExperiment.revalidate()
                usersEntries.put(user, userPanel)
            }

            override fun onRemoteUserFound(user: RemoteUser) {
                println("user found")
                val userPanel = createUserPanel(user)
                userPanel.background = UIManager.getColor("List.background")
                userPanel.isOpaque = true
                userPanel.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(mouseEvent: MouseEvent) {
                        super.mouseClicked(mouseEvent)
                        if (previousSelection != null)
                            previousSelection!!.background = UIManager.getColor("List.background")
                        previousSelection = userPanel
                        userPanel.background = UIManager.getColor("List.selectionBackground")
                        //userPanel.setOpaque(true);
                    }
                })
                compsToExperiment.add(userPanel)
                compsToExperiment.revalidate()
                usersEntries.put(user, userPanel)
            }

            override fun onTextMessageReceived(who: RemoteUser, message: String?) {
            }

            override fun onSendRequest(request: InRequest) {

            }

            override fun onSendResponse(allow: Boolean, transmission: TransmissionOut) {

            }

            override fun onUserDisconnected(remoteUser: RemoteUser?) {

                compsToExperiment.remove(usersEntries.remove(remoteUser))
                compsToExperiment.revalidate()
            }

            override fun onInternalError(code: Int, message: String?) {

            }
        }
        sendere.updateRemoteUsersList()
    }

    fun createUserPanel(user: RemoteUser): JPanel {
        val container = JPanel()
        container.layout = GridBagLayout()
        var constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridheight = 2
        constraints.ipadx = 45
        constraints.ipady = 45
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        val avatar = JButton()
        container.add(avatar, constraints)
        constraints = GridBagConstraints()
        constraints.anchor = GridBagConstraints.WEST
        constraints.gridheight = 1
        constraints.gridx = 1
        constraints.fill = GridBagConstraints.VERTICAL
        constraints.weightx = 100.0
        constraints.weighty = 1.0
        constraints.insets = Insets(0, 16, 0, 0)
        //constraints.anchor = GridBagConstraints.WEST;
        val nickname = JLabel(user.nickname)
        // nickname.setBorder(new EmptyBorder(16,16,16,16));
        container.add(nickname, constraints)
        constraints.gridy = 1
        val address = JLabel("at " + user.address + ":" + user.port)
        //nickname.setBorder(new EmptyBorder(16,16,16,16));
        container.border = EmptyBorder(8, 8, 8, 8)
        container.add(address, constraints)
        return container
    }
}
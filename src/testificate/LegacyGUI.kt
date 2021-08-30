package testificate

import sendereCommons.*
import sendereCommons.protopackets.RemoteErrorPacket
import java.awt.*
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder


class LegacyGUI {

    lateinit var sendere: Sendere
    var previousSelection: JPanel? = null
    var usersEntries = HashMap<RemoteUser, JComponent>()

    var addrezz = 23;

    fun init() {

        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName())
        } catch (e: UnsupportedLookAndFeelException) {
            // handle exception
        } catch (e: ClassNotFoundException) {
            // handle exception
        } catch (e: InstantiationException) {
            // handle exception
        } catch (e: IllegalAccessException) {
            // handle exception
        }

        Settings.nickname = "PSB133S01ZFP"

        val mainWindow = JFrame("Sendere")
        val tabbedPane = JTabbedPane()
        val compsToExperiment = GridLayoutDemo("Sendere")

        mainWindow.contentPane.layout = GridBagLayout()

        var constraints = GridBagConstraints()

        var usersJPanel = JPanel()
        var usersBoxLayout = BorderLayout()
        usersJPanel.layout = usersBoxLayout
        usersJPanel.add(compsToExperiment, BorderLayout.NORTH)
        tabbedPane.addTab("Клиенты", usersJPanel)

        tabbedPane.addTab("Передачи", JPanel())

        tabbedPane.addTab("Приём", JPanel())

        tabbedPane.addTab("Настройки", JPanel())

        mainWindow.contentPane.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.BOTH
        constraints.weightx = 0.5
        constraints.weighty = 0.5

        mainWindow.contentPane.add(tabbedPane, constraints)

        constraints.gridx = 0
        constraints.gridy = 1
        constraints.fill = GridBagConstraints.BOTH

        //mainWindow.contentPane.add(contentPane, constraints)

        mainWindow.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mainWindow.preferredSize = Dimension(800, 450)
        mainWindow.minimumSize = Dimension(350, 300)
        mainWindow.pack()
        mainWindow.setLocationRelativeTo(null)
        mainWindow.isVisible = true

        NetworkList.updateList()
//        sendere = object: Sendere(){
//            override fun onAuthenticationRequestReceived(secret: Long) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onRemoteErrorReceived(user: RemoteUser, errorType: RemoteErrorPacket.ErrorType?, extraMessage: String?) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onRemoteUserUpdated(user: RemoteUser) {
//
//            }
//
//            override fun onRemoteUserConnected(user: RemoteUser) {
//                println("user connected")
//                val userPanel = createUserPanel(user)
//                userPanel.background = UIManager.getColor("List.background")
//                userPanel.isOpaque = true
//                userPanel.addMouseListener(object : MouseAdapter() {
//                    override fun mouseClicked(mouseEvent: MouseEvent) {
//                        super.mouseClicked(mouseEvent)
//                        if (previousSelection != null)
//                            previousSelection!!.background = UIManager.getColor("List.background")
//                        previousSelection = userPanel
//                        userPanel.background = UIManager.getColor("List.selectionBackground")
//                        //userPanel.setOpaque(true);
//                    }
//                })
//                compsToExperiment.add(userPanel)
//                compsToExperiment.revalidate()
//                usersEntries.put(user, userPanel)
//            }
//
//            override fun onRemoteUserFound(user: RemoteUser) {
//                println("user found")
//                val userPanel = createUserPanel(user)
//                userPanel.background = UIManager.getColor("List.background")
//                userPanel.isOpaque = true
//                userPanel.addMouseListener(object : MouseAdapter() {
//                    override fun mouseClicked(mouseEvent: MouseEvent) {
//                        super.mouseClicked(mouseEvent)
//                        if (previousSelection != null)
//                            previousSelection!!.background = UIManager.getColor("List.background")
//                        previousSelection = userPanel
//                        userPanel.background = UIManager.getColor("List.selectionBackground")
//                        //userPanel.setOpaque(true);
//                    }
//                })
//                compsToExperiment.add(userPanel)
//                compsToExperiment.revalidate()
//                usersEntries.put(user, userPanel)
//            }
//
//            override fun onTextMessageReceived(who: RemoteUser, message: String?) {
//            }
//
//            override fun onSendRequest(request: InRequest) {
//
//            }
//
//            override fun onSendResponse(allow: Boolean, transmission: TransmissionOut) {
//
//            }
//
//            override fun onUserDisconnected(remoteUser: RemoteUser) {
//
//                compsToExperiment.remove(usersEntries.remove(remoteUser))
//                compsToExperiment.revalidate()
//            }
//
//            override fun onInternalError(code: Int, message: String?) {
//
//            }
//        }
//        //compsToExperiment.add(createTransmissionPanel("HukuToc2288",69, "zen_installer-2020.05.06-x86_64.iso"))
    }

    fun createUserPanel(user: RemoteUser): JPanel {
        val container = JPanel()
        var isConnected = (addrezz%2)>0
        container.layout = GridBagLayout()
        var constraints = GridBagConstraints()
        constraints.gridx = 1
        constraints.gridy = 0
        constraints.gridheight = 2
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        if (!isConnected) {
            val avatar = JButton("Сопряжение")
            container.add(avatar, constraints)
        } else {
            val avatar = JLabel("Сопряжено")
            container.add(avatar, constraints)
        }
        constraints = GridBagConstraints()
        constraints.anchor = GridBagConstraints.WEST
        constraints.gridheight = 1
        constraints.gridx = 0
        constraints.fill = GridBagConstraints.VERTICAL
        constraints.weightx = 100.0
        constraints.weighty = 1.0
        constraints.insets = Insets(0, 8, 0, 8)
        //constraints.anchor = GridBagConstraints.WEST;
        val nickname = JLabel(user.nickname)
        nickname.setFont(Font("Arial", Font.BOLD, 14))
        nickname.text = user.nickname
        // nickname.setBorder(new EmptyBorder(16,16,16,16));
        container.add(nickname, constraints)
        constraints.gridy = 1
        val address = JLabel("на 192.168.1." + addrezz++)
        //nickname.setBorder(new EmptyBorder(16,16,16,16));
        container.border = EmptyBorder(8, 8, 8, 8)
        container.add(address, constraints)
        return container
    }

    fun createTransmissionPanel(user: String, progress: Int, file: String): JPanel {
        val container = JPanel()
        var isConnected = (addrezz%2)>0
        container.layout = GridBagLayout()
        var constraints = GridBagConstraints()
        constraints.gridx = 1
        constraints.gridy = 0
        constraints.gridheight = 3
        constraints.weightx = 1.0
        constraints.weighty = 1.0
        constraints.ipadx = 14
        constraints.ipady = 14
        constraints.insets = Insets(4,4,4,4)
            val avatar = JButton("x!")
            container.add(avatar, constraints)
        constraints.gridx = 2
        container.add(JButton("x!"), constraints)

        container.background = UIManager.getColor("List.background")
        constraints = GridBagConstraints()
        constraints.anchor = GridBagConstraints.WEST
        constraints.gridheight = 1
        constraints.gridx = 0
        constraints.fill = GridBagConstraints.VERTICAL
        constraints.weightx = 100.0
        constraints.weighty = 1.0
        constraints.insets = Insets(4, 8, 4, 8)
        //constraints.anchor = GridBagConstraints.WEST;
        val nickname = JLabel(user)
        nickname.setFont(Font("Arial", Font.BOLD, 12))
        nickname.text = file
        // nickname.setBorder(new EmptyBorder(16,16,16,16));
        container.add(nickname, constraints)
        constraints.gridy = 1
        val address = JLabel("для "+user)
        //nickname.setBorder(new EmptyBorder(16,16,16,16));
        container.add(address, constraints)

        constraints = GridBagConstraints()
        constraints.gridy = 2
        constraints.gridx = 0
        constraints.fill = GridBagConstraints.HORIZONTAL
        container.border = EmptyBorder(8, 8, 8, 8)
        constraints.insets = Insets(0, 8, 0, 8)
        var progressBar = JProgressBar(0,100)
        progressBar.setSize(progressBar.width,1)
        constraints.ipady = 2
        constraints.ipadx = 50
        constraints.weighty = 1.0
        progressBar.value = progress
        progressBar.isStringPainted = true
        container.add(progressBar,constraints)
        return container
    }
}
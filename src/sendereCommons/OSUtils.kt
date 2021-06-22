package sendereCommons



private const val OS_UNSET = 0;
private const val OS_LINUX = 1;
private const val OS_WINDOWS = 2;
private const val OS_MAC = 3;
private const val OS_FREEBSD = 4;
private const val OS_ANDROID = 5;
private const val OS_IOS = 6;
private const val OS_UNIX = 7; //other unix

private const val OS_UNKNOWN = 99;

private var operationSystem = OS_UNSET;

public fun getOS(): Int {
    if (operationSystem == 0){
        val os = System.getProperty("os.name").toLowerCase();
        operationSystem = when{
            os.contains("linux") -> OS_LINUX
            os.contains("windows") -> OS_WINDOWS
            os.contains("mac") -> OS_MAC
            os.contains("bsd") -> OS_FREEBSD
            os.contains("android") -> OS_ANDROID
            os.contains("ios") -> OS_IOS
            os.contains("unix") -> OS_UNIX
            else -> OS_UNKNOWN
        }
    }
    return operationSystem;
}

class OSUtils {

}
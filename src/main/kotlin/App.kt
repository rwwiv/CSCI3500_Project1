import dfa.*
import java.io.File
import kotlin.system.exitProcess
import stringhandler.*

fun main(args: Array<String>) {
    val myDFA: DFA
    val stateList: MutableList<String>
    val alphaList: MutableList<Char>
    val transFunList: MutableList<Triple<String, Char, String>>
    val startString: String
    val finalList: MutableList<String>
    val lineList: List<String>
    val fileName: String

    fun readFile(fileName: String): List<String> {
        if (File(fileName).exists()) return File(fileName).bufferedReader().readLines()
        else {
            println("File does not exist")
            exitProcess(-1)
        }
    }

    /**
     * Dev test, to avoid packaging each iteration
    fileName = "test04.dfa"
    lineList = readFile(fileName)
     */

    if (args.isEmpty()) {
        println("Please input a file name")
        exitProcess(-1)
    }

    if (args[0].isNotEmpty()
            && args[0].toLowerCase().endsWith(".dfa")) {
        fileName = args[0]
        lineList = readFile(fileName)
    } else {
        println("Please input a valid file name")
        exitProcess(-1)
    }

    if (lineList.size == 5) {
        if (lineList[0].startsWith("(states")) {
            stateList = splitStringToListString(lineList[0], "states")
        } else {
            println("Invalid first line")
            exitProcess(-1)
        }
        if (lineList[1].startsWith("(alpha")) {
            alphaList = splitStringToListChar(lineList[1], "alpha")
        } else {
            println("Invalid second line")
            exitProcess(-1)
        }
        if (lineList[2].startsWith("(trans-func")) {
            transFunList = splitStringToListTriple(lineList[2], "trans-func")
        } else {
            println("Invalid third line")
            exitProcess(-1)
        }
        if (lineList[3].startsWith("(start")) {
            startString = splitStringToString(lineList[3], "start")
        } else {
            println("Invalid fourth line")
            exitProcess(-1)
        }
        if (lineList[4].startsWith("(final")) {
            finalList = splitStringToListString(lineList[4], "final")
        } else {
            println("Invalid fifth line")
            exitProcess(-1)
        }
    } else {
        println("Incorrect number of lines in dfa file")
        exitProcess(-1)
    }

    when {
        startString.isEmpty() -> {
            println("No start string found")
            exitProcess(-1)
        }
        stateList.isEmpty() -> {
            println("State lists is empty")
            exitProcess(-1)
        }
    }

    if (!stateList.contains(startString)) {
        println("Start state is not in list of states")
        exitProcess(-1)
    }

    fun dfaRunFun(q: String, a: Char): String {
        transFunList.forEachIndexed { index, triple ->
            if (triple.first == q) {
                if (triple.second == a) {
                    return triple.third
                }
            }
        }
        println("Incomplete dfa")
        exitProcess(-1)
    }

    if (!alphaList.contains(' ')) {
        for (item in transFunList) {
            if (!stateList.contains(item.first)
                    || !alphaList.contains(item.second)
                    || !stateList.contains(item.third)) {
                println("Invalid transition function")
                exitProcess(-1)
            }
            for (alpha in alphaList) {
                dfaRunFun(item.first, alpha)
            }
        }
    }

    if (finalList.contains("")) {
        if (finalList.size != 1) {
            println("Invalid list of final states")
            exitProcess(-1)
        }
    } else {
        for (item in finalList) {
            if (!stateList.contains(item)) {
                println("Final state exists in list of final states but not in list of states")
                exitProcess(-1)
            }
        }
    }

    myDFA = DFA(stateList,
            alphaList,
            transFunList,
            startString,
            finalList)

    println("Input DFA: ")
    prettyPrintDFA(myDFA)

    val newDFA = dfaMinAlgo(myDFA)

    println("\nMinimized DFA: ")
    prettyPrintDFA(newDFA)
}
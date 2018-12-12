package stringhandler

import kotlin.system.exitProcess

fun splitStringToString(input: String,
                        type: String): String {
    return input.removePrefix("($type, ")
            .removeSuffix(")")
}

fun splitStringToListString(input: String,
                            type: String): MutableList<String> {
    return input.removePrefix("($type, (")
            .removeSuffix(")")
            .split(",")
            .map { it.trim('(', ')', ' ') }
            .toMutableList()
}

fun splitStringToListChar(input: String,
                          type: String): MutableList<Char> {
    val tempListChar = mutableListOf<Char>()
    val tempListString = input.removePrefix("($type, (")
            .removeSuffix(")")
            .split(",")
            .map { it.trim('(', ')', ' ') }
    for (i in 0..(tempListString.size - 1)) {
        when {
            tempListString[i].isEmpty() && tempListChar.isEmpty() -> tempListChar.add(' ')
            tempListString[i].length == 1 -> tempListChar.add(tempListString[i][0])
            else -> {
                println("Invalid input in list of alphabet characters")
                exitProcess(-1)
            }
        }
    }
    return tempListChar
}

fun splitStringToListTriple(input: String,
                            type: String):
        MutableList<Triple<String, Char, String>> {
    val tempPairList = mutableListOf<Pair<String, Char>>()
    val triplesList = mutableListOf<Triple<String, Char, String>>()
    val tempStringList = input.removePrefix("($type, (")
            .removeSuffix(")")
            .split(",")
            .map { it.trim('(', ')', ' ') }
    if (tempStringList[0].isEmpty()) {
        if (!(tempStringList.size % 3 == 0)) {
            triplesList.add(Triple(" ", ' ', " "))
            return triplesList
        } else {
            println("Invalid transition function")
            exitProcess(-1)
        }
    }
    if (tempStringList.size % 3 == 0) {
        for (i in 0..(tempStringList.size - 1) step 3) {
            if (!tempStringList[i].isEmpty()
                    || tempStringList[i + 1].length == 1
                    || !tempStringList[i + 1].isEmpty()) {
                triplesList.add(Triple(
                        tempStringList[i],
                        tempStringList[i + 1][0],
                        tempStringList[i + 2]
                ))
            }
        }
        if (triplesList.distinct() == triplesList) return triplesList
        else {
            println("Transition function contains repeat transitions")
            exitProcess(-1)
        }
    } else {
        println("Invalid transition function")
        exitProcess(-1)
    }
}
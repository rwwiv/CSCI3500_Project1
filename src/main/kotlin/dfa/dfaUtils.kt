package dfa

import kotlin.system.exitProcess

fun prettyPrintDFA(input: DFA) {
    print("(states, ")
    println("${input.states.joinToString(",", "(", ")")})")
    print("(alpha, ")
    when {
        input.alpha.contains(' ') -> {
            println("())")
        }
        else -> {
            println("${input.alpha.joinToString(",", "(", ")")})")
        }
    }
    print("(trans-func, (")
    when {
        input.transFun.contains(Triple(" ", ' ', " ")) -> {
            println("()))")
        }
        else -> {
            for (i in 0..(input.transFun.size - 2)) {
                print(input.transFun[i].toList().joinToString(",", "(", ")"))
                print(",")
            }
            println("${input.transFun[input.transFun.size - 1].toList().joinToString(",", "(", ")")}))")
        }
    }
    println("(start, (${input.start}))")
    print("(final, ")
    println("${input.final.joinToString(",", "(", ")")})")
}

fun dfaMinAlgo(dfa: DFA): DFA {

    fun dfaRunFun(q: String, a: Char): String {
        dfa.transFun.forEachIndexed { index, triple ->
            if (triple.first == q) {
                if (triple.second == a) {
                    return triple.third
                }
            }
        }
        return ""
    }

    fun finalAndNotFinal(p: String, q: String): Boolean {
        return ((dfa.final.contains(p) != dfa.final.contains(q)))
    }

    val distinct = mutableListOf<MutableList<Boolean>>()
    var equivSetList = mutableListOf<MutableSet<String>>()

    dfa.states.forEachIndexed { pIndex, s ->
        distinct.add(mutableListOf())
        for (qIndex in 0..(pIndex - 1)) {
            distinct[pIndex].add(finalAndNotFinal(dfa.states[pIndex], dfa.states[qIndex]))
        }
    }

    val temp2DListComp = mutableListOf<MutableList<Boolean>>()
    var testBool: Boolean

    do {
        temp2DListComp.clear()
        distinct.forEachIndexed { mIndex, mutableList ->
            temp2DListComp.add(mutableListOf())
            mutableList.forEachIndexed { bIndex, b ->
                temp2DListComp[mIndex].add(b)
            }
        }

        distinct.forEachIndexed { pIndex, mutableList ->
            distinct[pIndex].forEachIndexed { qIndex, b ->
                for (alpha in dfa.alpha) {
                    val p = dfa.states[pIndex]
                    val q = dfa.states[qIndex]
                    var deltaPIndex = dfa.states.indexOf(dfaRunFun(p, alpha))
                    var deltaQIndex = dfa.states.indexOf(dfaRunFun(q, alpha))
                    if (deltaPIndex == deltaQIndex) continue
                    if (deltaPIndex < deltaQIndex) {
                        val oldDQI = deltaQIndex
                        deltaQIndex = deltaPIndex
                        deltaPIndex = oldDQI
                    }
                    if (distinct[deltaPIndex][deltaQIndex] && !distinct[pIndex][qIndex]) {
                        distinct[pIndex][qIndex] = true
                    }
                }
            }
        }
        testBool = (temp2DListComp != distinct)
    } while (testBool)

    distinct.forEachIndexed { pIndex, mutableList ->
        distinct[pIndex].forEachIndexed { qIndex, b ->
            if (!b) {
                equivSetList.add(mutableSetOf(dfa.states[pIndex], dfa.states[qIndex]))
                val tempSet = mutableSetOf<String>()
                equivSetList.forEachIndexed { sIndex, set ->
                    when {
                        set.contains(dfa.states[pIndex]) -> set.add(dfa.states[qIndex])
                        set.contains(dfa.states[qIndex]) -> set.add(dfa.states[pIndex])
                        else -> {
                            tempSet.add(dfa.states[pIndex])
                            tempSet.add(dfa.states[qIndex])
                        }
                    }
                }
                equivSetList.add(tempSet)
            }
        }
    }
    equivSetList = equivSetList.distinct().toMutableList()

    var newStates = dfa.states
    var newTransFun = dfa.transFun
    var newFinal = dfa.final

    for (set in equivSetList) {
        for (item in set) {
            if (dfa.states.contains(item)) {
                newStates[dfa.states.indexOf(item)] = set.toList().joinToString(",", "[", "]")
            }

            dfa.transFun.forEachIndexed { tIndex, triple ->
                if (triple.first == item) {
                    newTransFun[tIndex] = Triple(
                            set.toList().joinToString(",", "[", "]"),
                            dfa.transFun[tIndex].second,
                            dfa.transFun[tIndex].third
                    )
                }
                if (triple.third == item) {
                    newTransFun[tIndex] = Triple(
                            dfa.transFun[tIndex].first,
                            dfa.transFun[tIndex].second,
                            set.toList().joinToString(",", "[", "]")
                    )
                }
            }
            if (dfa.final.contains(item)) {
                newFinal[dfa.final.indexOf(item)] = set.toList().joinToString(",", "[", "]")
            }
        }
    }

    newStates = newStates.distinct().toMutableList()
    newTransFun = dfa.transFun.distinct().toMutableList()
    newFinal = dfa.final.distinct().toMutableList()

    return DFA(newStates,
            dfa.alpha,
            newTransFun,
            dfa.start,
            newFinal)
}

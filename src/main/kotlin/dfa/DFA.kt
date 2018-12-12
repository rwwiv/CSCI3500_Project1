package dfa

data class DFA(val states: MutableList<String>,
               val alpha: MutableList<Char>,
               val transFun: MutableList<Triple<String, Char, String>>,
               val start: String,
               val final: MutableList<String>)
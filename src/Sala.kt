class Sala constructor(val descricao: String, val opcoes: MutableList<Comando>){

    fun printDescricao() {
        println(descricao)
    }

    fun printOpcoes() {
        for ((j, i) in opcoes.withIndex()) {
            println("${j+1}: ${i.comando}")
        }
    }

    fun getOpcaoResultado(i: Int) {
        println(opcoes.get(i-1).resultado)
    }

    fun getOpcaoChave(i: Int): String {
        return opcoes.get(i-1).chave
    }

    fun getOpcaoSize(): Int {
        return opcoes.size
    }

}
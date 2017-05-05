import org.w3c.dom.*
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.system.exitProcess

class Mundo {

    private var map = LinkedHashMap<String, Sala?>()
    private var opcoes: MutableList<Comando> = mutableListOf()

    fun criarMundo(localArquivoSalas: String) {
        xmlParser(localArquivoSalas)
    }

    fun getSalas(): HashMap<String, Sala?> {
        return map
    }

    private fun xmlParser(localArquivoSalas: String) {
        try {
            val xmlFile: File = File(localArquivoSalas)
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(xmlFile)

            doc.documentElement.normalize()

            val nList: NodeList = doc.getElementsByTagName("sala")

            var i: Int = 0
            while (i < nList.length) {
                val nNode: Node = nList.item(i)
                if (nNode.nodeType == Node.ELEMENT_NODE) {
                    val eElement: Element = nNode as Element
                    var j: Int = 0
                    var descricaoComando: String
                    var resultadoComando: String
                    var keyComando: String

                    opcoes = arrayListOf()
                    while (j < eElement.getElementsByTagName("comando").length) {
                        descricaoComando = eElement.getElementsByTagName("comando").item(j).textContent
                        resultadoComando = eElement.getElementsByTagName("comando").item(j).attributes.item(1).textContent
                        keyComando = eElement.getElementsByTagName("comando").item(j).attributes.item(0).textContent
                        opcoes.add(j, Comando(descricaoComando, resultadoComando, keyComando))

                        val salaDescricao = eElement.getElementsByTagName("descricao").item(0).textContent
                        map.set(eElement.getAttribute("nome"), Sala(salaDescricao, opcoes))

                        j++
                    }
                }
                i++;
            }

        } catch (e: Exception) {
            //e.printStackTrace()
            println("Falha ao manusear o arquivo XML")
            exitProcess(1)
        }
    }

    fun iniciaJogo() {
        var opcaoEscolhida: String?
        var key: String? = "salaA" // Inicia com a Sala Inicial

        while (true) {
            map.get(key)?.printDescricao()
            map.get(key)?.printOpcoes()
            println("0: Finaliza o jogo")
            print("Opção: ")
            opcaoEscolhida = readLine()

            if (opcaoEscolhida.equals("0")) {
                println("Saiu do Jogo.")
                exitProcess(0)
            } else if (opcaoEscolhida!!.toInt() > map[key]!!.getOpcaoSize() || opcaoEscolhida.toInt() < 0) {
                println("Opção inexistente!")
            } else {
                map[key]?.getOpcaoResultado(opcaoEscolhida.toInt())
                println()
                key = map.get(key)?.getOpcaoChave(opcaoEscolhida.toInt())
                if (key.equals("nenhuma")) {
                    map[key]?.getOpcaoResultado(opcaoEscolhida.toInt())
                    println("Jogo Finalizado!")
                    exitProcess(0)
                } else if (key.equals("salaFinal")) {
                    map[key]?.printDescricao()
                    exitProcess(0)
                }
            }
        }
    }

    fun gerarGrafico() {
        val arquivo = FileWriter("datastruct.gv.txt")
        val gravarArquivo = PrintWriter(arquivo)
        var i: Int = 0
        var j: Int
        val keys = map.keys.toList()
        var comando: String
        val nodes = LinkedHashMap<String, String>()

        // Configuração do arquivo
        gravarArquivo.printf("digraph g {\n\tgraph [rankdir = \"LR\"];\n")
        gravarArquivo.printf("\tnode [fontsize = \"16\"];\n")

        // Cria os Nodes
        while (i < map.size) {
            nodes.set(keys[i], "node"+i)
            gravarArquivo.printf("\t\"node"+i+"\" [label = \"")
            gravarArquivo.printf("<f0> "+keys[i])
            j = 0
            while (j < map[keys[i]]!!.getOpcaoSize()) {
                comando = map[keys[i]]!!.opcoes.get(j).comando
                if (comando != "") {
                    gravarArquivo.printf(" | ")
                    gravarArquivo.append("<f"+(j+1)+"> "+comando.substring(0, 10)+"...")
                } else {
                    gravarArquivo.append(" ")
                }
                j++
            }
            gravarArquivo.append("\"")
            gravarArquivo.printf(" shape = \"record\"];\n")
            i++
        }

        // Faz a ligação dos Nodes
        i = 0
        var chave: String?
        while (i < map.size) {
            j = 0;
            while (j < map[keys[i]]!!.getOpcaoSize()) {
                chave = map[keys[i]]!!.opcoes[j].chave
                if (chave != "nenhuma" && chave != "") {
                    gravarArquivo.printf("\t\"node"+i+"\":f"+(j+1)+" -> \""+nodes[chave]+"\":f0;\n")
                }
                j++
            }
            i++
        }
        gravarArquivo.printf("}")
        arquivo.close()
    }
}
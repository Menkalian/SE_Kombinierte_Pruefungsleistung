import java.io.File
import java.io.FileWriter
import java.util.Scanner
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

fun main() {
    val rng = Random(2627585_4485500_2020L)

    // Load allowed chars for Baggage
    val allowedCharsInput = Scanner(File(Passenger::class.java.getResource("allowed_baggage_chars.txt").toURI()))
    val allowedChars = allowedCharsInput.nextLine().toCharArray().toList()

    // Read input
    val inputScanner = Scanner(File(Passenger::class.java.getResource("passenger_baggage.txt").toURI()))
    val passengers = mutableListOf<Passenger>()

    while (inputScanner.hasNextLine()) {
        val current = inputScanner.nextLine().split(";")
        val passengerIndex = passengerIndex.getAndIncrement()
        println("Generating data for Passenger #$passengerIndex")
        val currentPassenger = Passenger(current[0])
        passengers.add(currentPassenger)
        val amountBaggage = current[1].toInt()

        // Generate innocent baggages
        for (i in 0 until amountBaggage) {
            val baggageIndex = baggageIndex.getAndIncrement()
            println("Generating data for Baggage #$baggageIndex (Passenger: #$passengerIndex)")
            // generate plain Piece of Baggage
            val layers = mutableListOf<MutableList<Char>>()
            // 5 Layers
            for (j in 0..4) {
                val currentList = mutableListOf<Char>()
                for (k in 1..10_000) {
                    currentList.add(allowedChars[rng.nextInt(allowedChars.size)])
                }
                layers.add(currentList)
            }
            val currentBaggage = Baggage(baggageIndex, layers)
            currentPassenger.baggages.add(currentBaggage)
        }

        // Place forbidden data
        val placingInformation = current[2].substringAfter("[").substringBefore("]").split(";")

        placingInformation.forEach {
            if (it.length > 1) {
                val currentInformation = it.split(",")
                val signatureToPlace = when (currentInformation[0]) {
                    "W" -> "glock|7"
                    "K" -> "kn!fe"
                    "E" -> "exp|os!ve"
                    else -> throw RuntimeException("unknown signature")
                }
                val indexToPlace = rng.nextInt(currentPassenger.baggages[currentInformation[1].toInt() - 1].data[currentInformation[2].toInt() - 1].size - signatureToPlace.length)
                for (i in 0 until signatureToPlace.length) {
                    currentPassenger.baggages[currentInformation[1].toInt() - 1].data[currentInformation[2].toInt() - 1][indexToPlace + i] = signatureToPlace[i]
                }
            }
        }
    }
    println("Finished generating data. Generated data for ${passengerIndex.get()} Passengers and ${baggageIndex.get()} Baggages.")
    println()
    val outputDir = File("template/02_implementation/src/main/resources/security/simulation")
    if (outputDir.mkdirs()) {
        println("Created output directory.")
    }
    println("Generating the outputs to output directory ${outputDir.absolutePath}")
    println("Passenger List with matching Baggages to passengers.txt")
    val passengerListOutput = FileWriter(outputDir.path + "/passengers.txt", false)
    passengers.forEach {
        val stringToWrite = StringBuilder()
        stringToWrite.append(it.name + ";")
        it.baggages.forEach { bag -> stringToWrite.append(bag.number.toString() + ";") }
        passengerListOutput.write(stringToWrite.trim(';').toString())
        passengerListOutput.write("\n")
    }
    passengerListOutput.close()

    println("All Baggages to their own file baggage_***.txt")
    passengers.forEach {
        it.baggages.forEach { bag ->
            val baggageOutput = FileWriter(outputDir.path + "/baggage_${bag.number}.txt", false)
            bag.data.forEach { layer ->
                layer.forEach { c -> baggageOutput.append(c) }
                baggageOutput.append("\n")
            }
            baggageOutput.close()
        }
    }

    println("Finished writing data. We are done here.")
}

val passengerIndex = AtomicInteger(0)
val baggageIndex = AtomicInteger(0)

data class Passenger(val name: String, val baggages: MutableList<Baggage> = mutableListOf())

data class Baggage(val number: Int, val data: List<MutableList<Char>>)
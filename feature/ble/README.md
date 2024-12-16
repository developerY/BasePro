BLE Scanner Example
With Flow:
[example](https://github.com/santansarah/ble-scanner)
Juul Labs:
[Juul Labs](https://github.com/JuulLabs/sensortag)
Using kotlin coroutines:
[exmple](https://github.com/millerGrey/BLE-android-example)

Correct:
[Nortec](https://github.com/NordicSemiconductor/Android-BLE-Library)
[KBLE](https://bitbucket.org/developerY/workspace/projects/KBEAUT)

Use this
[Best Example](https://github.com/MatthiasKerat/BLETutorialYt)
[Video](https://www.youtube.com/watch?v=qyG-SDfYNBE)
### **Comparison: Which Uses Kotlin Better?**

| Feature                     | **Nordic BLE Library**                                    | **KBeautifuLE**                                   |
|-----------------------------|-----------------------------------------------------------|--------------------------------------------------|
| **Kotlin Coroutines**        | ✅ Fully supported for async operations                    | ✅ Fully supported                               |
| **Kotlin Flows**             | ✅ Uses Flows for BLE streams                              | ✅ Explicitly uses Flows and Channels            |
| **Ease of Use**              | ✅ High abstraction, easy to integrate                     | ❌ Requires more manual setup and understanding |
| **Control over BLE**         | ❌ Abstracted away                                         | ✅ Fine-grained control                          |
| **Production-Readiness**     | ✅ Well-tested and widely used in production               | ❌ Focused on demonstrating Kotlin features      |
| **Learning Experience**      | ❌ Abstracts much of the Kotlin features                   | ✅ Hands-on with Kotlin features                 |
| **Jetpack Compose Integration** | ✅ Works well with Compose but requires manual integration | ✅ Built from scratch for Compose                |

---

### How This Differs from Regular BLE Scanning

| **Feature**                         | **Companion Device Pairing API**                           | **Regular BLE Scanning**                                |
|-------------------------------------|----------------------------------------------------------|-------------------------------------------------------|
| **Purpose**                         | Long-term trusted pairing for companion devices          | General-purpose device discovery                      |
| **Permission Requirements**         | No fine location permission                              | Requires fine location permissions                   |
| **Built-in User Interface**         | Yes                                                      | No                                                    |
| **Device Scope**                    | Target specific device based on user action              | Discovers all nearby BLE devices                     |
| **Use Case**                        | Persistent trusted devices (e.g., fitness tracker)       | Ad hoc or dynamic connections (e.g., nearby devices) |

---

Nordic database for BLE.
Make SQL Database with DB Browser

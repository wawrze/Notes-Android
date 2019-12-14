package pl.wawra.notes.utils

import android.os.Handler
import java.util.concurrent.Executors

private val executorBg = Executors.newSingleThreadExecutor()
fun onBg(run: () -> Unit) = executorBg.execute(run)

private val handler = Handler()
fun onUi(run: () -> Unit) = handler.post(run)
/*
 * Copyright (C) 2012 The Regents of The University California. 
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shark.repl

import java.io.PrintWriter

import shark.{SharkContext, SharkEnv}

import spark.{SparkContext, SparkEnv}
import spark.repl.SparkILoop


/**
 * Add more Shark specific initializations.
 */
class SharkILoop extends SparkILoop(None, new PrintWriter(Console.out, true), None) {

  // Forces initializing the Kryo register. If we don't make the call explicit,
  // there is no guarantee that SharkEnv's static section is executed before
  // the Kryo serializer is initialized by Spark.
  SharkEnv.initWithSharkContext("shark-shell")

  override def initializeSpark() {
    intp.beQuietDuring {
      command("""
        spark.repl.Main.interp.out.println("Creating SparkContext...");
        spark.repl.Main.interp.out.flush();
        @transient val sparkContext = shark.SharkEnv.sc;
        @transient val sc = sparkContext.asInstanceOf[shark.SharkContext];
        spark.repl.Main.interp.out.println("Shark context available as sc.");
        import sc._;
        def s = sql2console _;
        spark.repl.Main.interp.out.flush();
        """)
      command("import spark.SparkContext._");
    }
    Console.println("Type in expressions to have them evaluated.")
    Console.println("Type :help for more information.")
    Console.flush()
  }
}


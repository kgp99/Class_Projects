package edu.asu.bsse.kgpatel6.lab6;

/*
 * Copyright (c) 2020 Tim Lindquist,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: Example Android application that uses an AsyncTask to accomplish the same effect
 * as using a Thread and android.os.Handler
 *
 * Ser423 Mobile Applications
 * see http://pooh.poly.asu.edu/Mobile
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version February 3, 2020
 */

import androidx.appcompat.app.AppCompatActivity;

public class MethodInformation {
    public int camefrom;
    public String method;
    public Object[] params;
    public AppCompatActivity parent;
    public String urlString;
    public String resultAsJson;

    MethodInformation(AppCompatActivity parent, String urlString, String method, Object[] params, int tempcamefrom){
        this.method = method;
        this.parent = parent;
        this.urlString = urlString;
        this.params = params;
        this.camefrom = tempcamefrom;
        this.resultAsJson = "{}";
    }
}


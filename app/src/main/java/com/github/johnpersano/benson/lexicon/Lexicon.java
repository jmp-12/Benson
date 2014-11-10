/**
 * Copyright 2014 John Persano
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
 */


package com.github.johnpersano.benson.lexicon;

import com.github.johnpersano.benson.lexicon.modules.Component;
import com.github.johnpersano.benson.lexicon.modules.Time;
import com.github.johnpersano.benson.lexicon.modules.Hello;
import com.github.johnpersano.benson.lexicon.modules.HowAreYou;
import com.github.johnpersano.benson.lexicon.modules.Joke;

import java.util.Arrays;
import java.util.List;

/* This class holds the default lexicon for Benson. If any new modules are added, be sure to add them here as well. */
public class Lexicon {

    public final List<? extends Query> lexicon = Arrays.asList(new Component(), new Hello(), new HowAreYou(), new Joke(), new Time());

}

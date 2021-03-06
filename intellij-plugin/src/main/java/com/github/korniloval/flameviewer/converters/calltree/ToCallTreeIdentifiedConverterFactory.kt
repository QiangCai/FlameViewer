package com.github.korniloval.flameviewer.converters.calltree

import com.github.kornilova_l.flamegraph.proto.TreesProtos
import com.github.korniloval.flameviewer.converters.IdentifiedConverterFactory

interface ToCallTreeIdentifiedConverterFactory : IdentifiedConverterFactory<TreesProtos.Trees>
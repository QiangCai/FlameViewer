package com.github.korniloval.flameviewer.server.handlers

import com.github.korniloval.flameviewer.FlameLogger
import com.github.korniloval.flameviewer.server.FindFile
import com.github.korniloval.flameviewer.server.RequestHandler
import com.github.korniloval.flameviewer.server.RequestHandlingException
import com.github.korniloval.flameviewer.server.ServerUtil.sendJson
import com.github.korniloval.flameviewer.server.TreeManager
import com.google.gson.Gson
import com.sun.xml.internal.ws.handler.HandlerException
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.QueryStringDecoder

class HotSpotsHandler(private val treeManager: TreeManager, private val logger: FlameLogger, private val findFile: FindFile) : RequestHandler {
    @Throws(RequestHandlingException::class)
    override fun process(request: FullHttpRequest, ctx: ChannelHandlerContext): Boolean {
        val decoder = QueryStringDecoder(request.uri())
        val file = findFile(getFileName(decoder)) ?: throw HandlerException("File not found. Uri: ${decoder.uri()}")
        val hotSpots = treeManager.getHotSpots(file)
        sendJson(ctx, Gson().toJson(hotSpots), logger)
        return true
    }
}
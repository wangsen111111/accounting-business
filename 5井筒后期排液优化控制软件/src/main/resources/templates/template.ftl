${(file.fileName)!} 文件笔记
文件信息    进度：${(file.progressOf)!} %  评分：${(file.score)!} 星

<#list fileNotes as fileNote>
【作者】${(fileNote.createUser)!} 【关键字】<#if fileNote.keywords?? && (fileNote.keywords?size>0)><#list fileNote.keywords as kewword>${kewword},</#list></#if> 【创建时间】<#if (fileNote.updateTime)??>${fileNote.updateTime ? string("yyyy-MM-dd HH:mm:ss")}</#if>
--------------------------------笔记${fileNote_index + 1}正文---------------------------------
${(fileNote.content)!}


</#list>
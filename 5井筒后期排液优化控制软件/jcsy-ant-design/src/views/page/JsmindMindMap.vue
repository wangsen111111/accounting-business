<template>
  <div id="app">
    <a-row style="background: #424a58">
      <a-button class="button-menu" type="link" @click="menu('file')">文件</a-button>
      <a-button class="button-menu" type="link" @click="menu('insert')">插入</a-button>
      <a-button class="button-menu" type="link" @click="menu('task')">任务</a-button>
      <a-button class="button-menu" type="link" @click="menu('view')">视图</a-button>
      <a-button class="button-menu" type="link" @click="menu('all')">全部</a-button>
      <a-button class="button-menu" type="link" @click="menu('associated')">关联</a-button>
    </a-row>
    <div>
      <!-- 快捷菜单 -->
      <div class="shortcut-menu">
        <div class="display-line">
          <div style="display: inline-block; margin-left: 10px" v-if="currSelectJsmind && currSelectJsmind.editable">
            <div style="width: 50px">
              <lockIcon :width="'100%'" :height="'42px'" alt="word" @click="toggleIsEditable(false)" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">禁用</div>
            </div>
          </div>
          <div class="display-line" v-if="!(currSelectJsmind && currSelectJsmind.editable)">
            <div style="width: 50px">
              <unlockIcon :width="'100%'" :height="'42px'" alt="word" @click="toggleIsEditable(true)" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">启用</div>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">节点编辑</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid"></div>
        <div class="display-line">
          <div style="display: inline-block; margin-left: 10px" v-if="!currSelectJsmind">
            <div style="width: 50px">
              <fullIcon :width="'100%'" :height="'42px'" alt="word" @click="toggleMode('full')" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">两侧</div>
            </div>
          </div>
          <div style="display: inline-block; margin-left: 10px" v-if="currSelectJsmind && currSelectJsmind.mode === 'side'" >
            <div style="width: 50px">
              <fullIcon :width="'100%'" :height="'42px'" alt="word" @click="toggleMode('full')" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">两侧</div>
            </div>
          </div>
          <div class="display-line" v-if="currSelectJsmind && currSelectJsmind.mode === 'full'">
            <div style="width: 50px">
              <sideIcon :width="'100%'" :height="'42px'" alt="word" @click="toggleMode('side')" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">右侧</div>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">模式</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid"></div>

        <!-- 主题开始 -->
        <div class="display-line" style="margin-left: 0px" v-if="menuType.indexOf('insert') !== -1">
          <!-- <div class="display-line">
          <div style="width:50px;">
            <sameLevelIcon :width="'100%'" :height="'42px'" alt="word" @click="addNodeAfter" />
            <a-dropdown>
              <div style="font-size:8px;cursor:pointer" type="flex" justify="center" align="middle">主题 <a-icon type="down" /> </div>
              <a-menu slot="overlay">
                <a-menu-item> <a @click="addNodeBefore">在指定位置前插入节点</a> </a-menu-item>
                <a-menu-item> <a @click="addNodeAfter">在指定位置后插入节点</a> </a-menu-item>
              </a-menu>
            </a-dropdown>
          </div>
        </div> -->
          <div class="display-line">
            <div style="width: 50px">
              <childLevelIcon :width="'100%'" :height="'42px'" alt="word" @click="addNode" />
              <!-- <div style="font-size:8px" type="flex" justify="center" align="middle">子主题</div> -->
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  主题 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="addNode(0)">子主题</a> </a-menu-item>
                  <a-menu-item> <a @click="addNode(1)">在指定位置前插入节点</a> </a-menu-item>
                  <a-menu-item> <a @click="addNode(2)">在指定位置后插入节点</a> </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <removeIcon :width="'100%'" :height="'42px'" alt="word" @click="removeNode" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">删除</div>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">主题</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('insert') !== -1"></div>
        <!-- 主题结束 -->

        <!-- 任务开始 -->
        <div class="display-line" style="margin-left: 0px" v-if="menuType.indexOf('task') !== -1">
          <!-- <div class="display-line">
          <div style="width:50px;">
            <progressOfIcon :width="'100%'" :height="'42px'" alt="word" />
            <div style="font-size:8px" type="flex" justify="center" align="middle">进度</div>
          </div>
        </div> -->
          <!-- <div class="display-line">
          <div style="width:50px;">
            <inputIcon :width="'100%'" :height="'42px'" alt="word" />
            <div style="font-size:8px" type="flex" justify="center" align="middle">上传</div>
          </div>
        </div> -->
          <!-- seeIcon -->
          <div class="display-line">
            <div style="width: 50px">
              <seeIcon :width="'100%'" :height="'42px'" alt="word" @click="see" />
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  查看 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="see(0)">查看全部文件</a> </a-menu-item>
                  <a-menu-item> <a @click="see(1)">查看当前节点文件</a></a-menu-item>
                  <a-menu-item> <a @click="see(2)">查看该思维导图全部文件</a> </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <outputIcon :width="'100%'" :height="'42px'" alt="word" />
              <!-- <div style="font-size: 8px" type="flex" justify="center" align="middle">下载</div> -->
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  下载 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="download(0)">思维导图</a> </a-menu-item>
                  <a-menu-item> <a @click="download(1)">该节点及其子孙文件</a> </a-menu-item>
                  <a-menu-item> <a @click="download(2)">该节点文件</a> </a-menu-item>
                  <a-menu-item> <a @click="download(3)">指定为文件</a> </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <div style="display: inline-block; margin-left: 10px">
            <div style="width: 50px">
              <mountIcon :width="'100%'" :height="'42px'" alt="word" @click="mount(2)" />
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  挂载 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="mount(0)">外链</a> </a-menu-item>
                  <a-menu-item> <a @click="mount(1)">上传</a> </a-menu-item>
                  <a-menu-item> <a @click="mount(2)">已有文件</a> </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <uninstallIcon :width="'100%'" :height="'42px'" alt="word" @click="unmount(0)" />
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  卸载 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="unmount(0)">卸载指定文件</a> </a-menu-item>
                  <a-menu-item> <a @click="unmount(1)">卸载当前节点</a> </a-menu-item>
                  <a-menu-item> <a @click="unmount(2)">卸载当前节点及子节点</a> </a-menu-item>
                  <a-menu-item> <a @click="unmount(3)">卸载整个思维导图</a> </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">任务</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('task') !== -1"></div>
        <!-- 任务结束 -->

        <!-- 标记开始 -->
        <div class="display-line" style="margin-left: 0px" v-if="menuType.indexOf('task') !== -1">
          <div class="display-line">
            <div style="width: 50px">
              <tagIcon :width="'100%'" :height="'42px'" alt="word" />
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  标签 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="openTag('0')">标签</a> </a-menu-item>
                  <a-menu-item> <a @click="openTag('1')">项目</a>  </a-menu-item>
                  <a-menu-item> <a @click="openTag('2')">标签组</a>  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <a-popover trigger="click" placement="bottom">
                <div slot="content" style="width:210px">
                  <a-divider>优先级</a-divider>
                  <div class="display-line" style="margin-left: 0px" >
                    <div class="display-line" style="width: 30px;margin-left:5px">
                      <img style="width:100%;height:28px" src="/svg/icon/close.svg" @click="deleteIcon('priority')">
                    </div>
                    <div class="display-line" style="width: 30px;margin-left:5px" v-for="priority in imgIcons['优先级']" :key="priority.level">
                      <img style="width:100%;height:25px" v-if="imgIcons" :src="priority.imageUrl" @click="insertIcon(priority)"/>
                    </div>
                  </div>
                  <a-divider>进度</a-divider>
                  <div class="display-line" style="margin-left: 0px" >
                    <div class="display-line" style="width: 30px;margin-left:5px">
                      <img style="width:100%;height:28px" src="/svg/icon/close.svg" @click="deleteIcon('progress')">
                    </div>
                    <div class="display-line" style="width: 30px;margin-left:5px" v-for="progressOf in imgIcons['进度']" :key="progressOf.level">
                      <img style="width:100%;height:25px" v-if="imgIcons" :src="progressOf.imageUrl" @click="insertIcon(progressOf)"/>
                    </div>
                  </div>
                </div>
                <tagIconIcon :width="'100%'" :height="'42px'" alt="word" />
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  图标 <a-icon type="down" />
                </div>
              </a-popover>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">标记</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('task') !== -1"></div>
        <!-- 标记结束 -->

        <!-- 详图开始 -->
        <div class="display-line" style="margin-left: 0px" v-if="menuType.indexOf('view') !== -1">
          <div class="display-line">
            <div style="width: 50px">
              <shrinkIcon :width="'100%'" :height="'42px'" alt="word" @click="toggleNode" />
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  收缩 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="expandAllNode">展开全部子节点</a> </a-menu-item>
                  <a-menu-item> <a @click="collapseNode">收起子节点</a> </a-menu-item>
                  <a-menu-item> <a @click="expandNode">展开子节点</a> </a-menu-item>
                  <!-- <a-menu-item> <a href="javascript:;">展开至层级</a> </a-menu-item> -->
                  <a-sub-menu key="test" title="展开至层级">
                    <a-menu-item @click="expandToDepth(1)">1级</a-menu-item>
                    <a-menu-item @click="expandToDepth(2)">2级</a-menu-item>
                    <a-menu-item @click="expandToDepth(3)">3级</a-menu-item>
                    <a-menu-item @click="expandToDepth(5)">5级</a-menu-item>
                    <a-menu-item @click="expandToDepth(7)">7级</a-menu-item>
                  </a-sub-menu>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <!-- <div class="display-line">
          <div style="width:50px;">
            <openIcon :width="'100%'" :height="'42px'" alt="word" @click="expandNode" />
            <div style="font-size:8px" type="flex" justify="center" align="middle">展开</div>
          </div>
        </div>
        <div class="display-line">
          <div style="width:50px;">
            <closeIcon :width="'100%'" :height="'42px'" alt="word" @click="collapseNode" />
            <div style="font-size:8px" type="flex" justify="center" align="middle">收起</div>
          </div>
        </div> -->
          <div class="display-line">
            <div style="width: 50px">
              <a-popover trigger="click">
                <div slot="content" style="width: 200px">
                  <a-slider id="test" v-model="proportion" :min="0.5" :max="3" :step="0.01" />
                </div>
                <a-icon type="zoom-in" :style="{ fontSize: '50px', color: '#08c' }" />
                <div style="font-size: 8px" type="flex" justify="center" align="middle">缩放</div>
              </a-popover>
            </div>
          </div>
          <!-- <div class="display-line">
          <div style="width:50px;">
            <a-icon type="zoom-out" :style="{ fontSize: '50px', color: '#08c' }" @click="zoomOut"/>
            <div style="font-size:8px" type="flex" justify="center" align="middle">缩小</div>
          </div>
        </div> -->
          <div type="flex" justify="center" align="middle">详图</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('view') !== -1"></div>
        <!-- 详图结束 -->

        <!-- 字体开始 -->
        <div class="display-line" v-if="menuType.indexOf('view') !== -1">
          <a-select label-in-value :default-value="{ key: '微软雅黑' }" style="width: 120px" @change="changeFamily">
            <a-select-option
              v-for="(fontFamily, index) in fontFamilys"
              :key="index"
              :value="fontFamily.value" ><span>{{ fontFamily.key }}</span>
            </a-select-option >
          </a-select>
          <a-input-number id="inputNumber" v-model="fontSize" :min="1" :max="70" style="width: 60px" />
          <!-- <a-select label-in-value :default-value="{ key: '12' }" style="width: 60px" @change="changeFontSize">
          <a-select-option
            :disabled="!selectNodeStatus ? true : false"
            v-for="(fontSize, index) in fontSizes"
            :key="index"
            :value="fontSize"
          >{{ fontSize }}</a-select-option>
        </a-select> -->
          <div>
            <span title="加粗" class="font-style" style="margin-left: 0px" @click="toggleFontThickness">B</span>
            <span title="斜体" class="font-style" @click="toggleFontItalic"><i>I</i> </span>
            <span title="下划线" class="font-style" @click="toggleUnderline"><u style="#f89">U</u> </span>
            <span title="删除线" class="font-style" @click="toggleLineThrough"><s>T</s> </span>
            <a-popover trigger="click">
              <Chrome slot="content" v-model="fontColor" />
              <span title="字体颜色" class="font-style" ><u id="fontColorStyle" :style="{ color: fontColor.hex }">A</u></span >
            </a-popover>
          </div>
          <div type="flex" justify="center" align="middle">字体</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('view') !== -1"></div>
        <!-- 字体结束 -->

        <!-- 对象格式开始 -->
        <div class="display-line" v-if="menuType.indexOf('view') !== -1">
          <div class="display-line">
            <a-dropdown>
              <div style="width: 50px">
                <themeIcon :width="'100%'" :height="'42px'" alt="word" />
                <div style="font-size: 8px" type="flex" justify="center" align="middle">主题样式</div>
              </div>
              <a-menu slot="overlay">
                <a-menu-item v-for="(theme, index) in themes" :key="index">
                  <a @click="changeTheme(theme)">{{ theme }}</a>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </div>
          <div class="display-line" @click="development">
            <div style="width: 50px">
              <shapeOfIcon :width="'100%'" :height="'42px'" alt="word" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">主题形状</div>
            </div>
          </div>
          <div style="display: inline-block; margin-left: 10px">
            <div style="width: 50px">
              <a-popover trigger="click" :disabled="!selectNodeStatus ? true : false">
                <Chrome slot="content" v-model="backgroundColor" />
                <fillColorIcon :width="'100%'" :height="'42px'" alt="word" :disabled="!selectNodeStatus ? true : false" />
                <div
                  :disabled="!selectNodeStatus ? true : false"
                  style="font-size: 8px"
                  type="flex"
                  justify="center"
                  align="middle" >
                  填充颜色
                </div>
              </a-popover>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <a-popover trigger="click">
                <Chrome slot="content" v-model="lineColor" />
                <lineColorIcon :width="'100%'" :height="'42px'" alt="word" />
                <div :style="{ 'font-size': '8px', color: lineColor.hex }" type="flex" justify="center" align="middle">
                  线条颜色
                </div>
              </a-popover>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <a-popover trigger="click">
                <div slot="content" style="width: 200px">
                  <a-slider id="test" v-model="lineThickness" :min="1" :max="8" />
                </div>
                <lineIcon :width="'100%'" :height="'42px'" alt="word" />
                <div style="font-size: 8px" type="flex" justify="center" align="middle">线条</div>
              </a-popover>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">对象格式</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('view') !== -1"></div>
        <!-- 对象格式结束 -->

        <!-- 点线布局距离开始 -->
        <div class="display-line" v-if="menuType.indexOf('view') !== -1">
          <div class="display-line">
            <div style="width: 50px">
              <a-popover trigger="click">
                <div slot="content" style="width: 200px"><a-slider id="test" v-model="hspace" :min="1" :max="100" /></div>
                <hspaceIcon :width="'100%'" :height="'42px'" alt="word" />
                <div style="font-size: 8px" type="flex" justify="center" align="middle">节点水平</div>
              </a-popover>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <a-popover trigger="click">
                <div slot="content" style="width: 200px"><a-slider id="test" v-model="vspace" :min="1" :max="100" /></div>
                <vspaceIcon :width="'100%'" :height="'42px'" alt="word" />
                <div style="font-size: 8px" type="flex" justify="center" align="middle">节点垂直</div>
              </a-popover>
            </div>
          </div>
          <!-- <div class="display-line">
          <div style="width:50px;">
            <a-popover trigger="click">
              <div slot="content" style="width:200px"> <a-slider id="test" v-model="pspace" :min="1" :max="100" /> </div>
              <pspaceIcon :width="'100%'" :height="'42px'" alt="word" />
              <div style="font-size:8px" type="flex" justify="center" align="middle">点线水平</div>
            </a-popover>
          </div>
        </div> -->
          <div type="flex" justify="center" align="middle">点线布局距离</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('view') !== -1"></div>
        <!-- 点线布局距离结束 -->

        <!-- 思维导图开始 -->
        <div class="display-line" v-if="menuType.indexOf('task') !== -1">
          <div style="display: inline-block; margin-left: 10px">
            <div style="width: 50px">
              <a-upload :file-list="fileList" name="file" :showUploadList="false" :before-upload="inputFile">
                <jsmindInputIcon :width="'100%'" :height="'42px'" alt="word" />
                <div style="font-size: 8px" type="flex" justify="center" align="middle">导入</div>
              </a-upload>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <jsmindOutputIcon :width="'100%'" :height="'42px'" alt="word" @click="outputFile" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">导出</div>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <closeIcon :width="'100%'" :height="'42px'" alt="word" @click="closeJsmind(0)" />
              <a-dropdown>
                <div style="font-size: 8px; cursor: pointer" type="flex" justify="center" align="middle">
                  关闭 <a-icon type="down" />
                </div>
                <a-menu slot="overlay">
                  <a-menu-item> <a @click="closeJsmind(0)">关闭当前</a> </a-menu-item>
                  <a-menu-item> <a @click="closeJsmind(1)">关闭右侧</a> </a-menu-item>
                  <a-menu-item> <a @click="closeJsmind(2)">关闭左侧</a> </a-menu-item>
                </a-menu>
              </a-dropdown>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">思维导图</div>
        </div>
        <div class="display-line" style="height: 90px; width: 1px; border-left: 1px #000 solid" v-if="menuType.indexOf('task') !== -1"></div>
        <!-- 思维导图结束 -->
        <!-- v-if="menuType.indexOf('task') !== -1" -->
        <div class="display-line" >
          <div style="display: inline-block; margin-left: 10px">
            <div style="width: 50px">
              <saveMindMapIcon :width="'100%'" :height="'42px'" alt="word" @click="savaMindMap" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">保存</div>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <saveTemplateIcon :width="'100%'" :height="'42px'" alt="word" @click="saveMindMapToTemplate" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">保存模板</div>
            </div>
          </div>
          <div class="display-line">
            <div style="width: 50px">
              <deleteIcon :width="'100%'" :height="'42px'" alt="word" @click="deleteMindMap" />
              <div style="font-size: 8px" type="flex" justify="center" align="middle">删除</div>
            </div>
          </div>
          <div type="flex" justify="center" align="middle">保存</div>
        </div>
      </div>
    </div>

    <!-- 思维导图 -->
    <a-row>
      <!-- <a-col :span="4">
        <div>标签：</div>
        <a-select
          mode="tags"
          placeholder="标签选择"
          style="width: 100%" >
          <a-select-option v-for="tag in tags" :key="String(tag.tagId)"> {{ tag.tagName }} </a-select-option>
        </a-select>
        <a-checkbox v-for="tag in tags" :key="tag.id">
          <span >{{ tag.tagName }}</span>
        </a-checkbox>
        <div>项目筛选条件：</div>
        <div>文件筛选条件：</div>
      </a-col> -->
      <a-col :span="24">
        <!--           :fileIds="selectedRowKeys"   @changeMountStatus="changeMountStatus" :tab="mindMap.mindMapName"-->
        <JsMindMap
          v-for="mindMap in currMindMapsComputed"
          :key="mindMap.id"
          :ref="mindMap.id"
          @isSelectNode="isSelectNode"
          :jsMindId="mindMap.id"
          :jsMindMapData="mindMap"
          :styleCss="{ width: '100%', height: 'calc(100vh - 255px)' }" >
        </JsMindMap>
        <!-- @change="changeMindMap" -->
        <a-tabs v-model="activeKey" tab-position="bottom" type="editable-card" @edit="onEdit">
          <a-tab-pane v-for="mindMap in mindMaps" :key="mindMap.id" :tab="mindMap.mindMapName">
          </a-tab-pane>
        </a-tabs>
      </a-col>
    </a-row>

    <!-- 文件菜单抽屉 -->
    <a-drawer
      :bodyStyle="{ padding: '0px', height: 'calc(100vh)' }"
      width="calc(33vw)"
      placement="left"
      :closable="false"
      :visible="drawerVisible"
      @close="drawerVisible = false" >
      <a-col :span="4" style="height: calc(100vh); background: #424a58">
        <returnIcon :width="'100%'" :height="'42px'" alt="word" style="margin-top: 30px" />
        <!-- @mouseenter="mouseLoc = index" -->
        <a
          style="font-color: #424a58"
          v-for="(fileMenu, index) in fileMenus"
          @click="menuTitile = fileMenu,mouseLoc = index"
          :key="index" >
          <h1 :class="index === mouseLoc ? 'mouseLoc' : ''" type="flex" justify="center" align="middle">
            {{ fileMenu }}
          </h1>
        </a>
      </a-col>
      <a-col :span="20">
        <h1 style="margin-top: 20px; margin-left: 30px; font-size: 30px">{{ menuTitile }}</h1>
        <div v-if="menuTitile === '新建'">
          <a style="margin-top: 0px; font-size: 20px" @click="newBuildMindMapBefore(null)"> <a-icon type="fork" />空思维导图</a>
          <h1 style="margin-top: 10px; margin-left: 30px; font-size: 30px">模板：</h1>
          <div style="margin-top: 10px; font-size: 14px; height: calc(80vh + 10px);width: '550px">
            <happy-scroll size="5" resize>
              <div v-for="(template, index) in jsmindTemplates" :key="index">
                <a style="font-size: 20px" @click="newBuildMindMapBefore(template)">  <a-icon type="fork" />{{ template.mindMapName }}</a>
                <a style="font-size: 20px;margin-left:20px" @click="()=>{seeTemplateVisible = true,currtemplate=template }"> 查看 </a>
                <!-- <a style="font-size: 20px;margin-left:20px" @click="editTemplate(template)"> 编辑 </a> -->
                <a style="font-size: 20px;margin-left:20px" @click="deleteTemplate(template)"> 删除 </a>
                <!-- <JsMindMap
                  :ref="template.id"
                  :jsMindId="template.id"
                  :jsMindMapData="template"
                  :styleCss="{ width: '550px', height: '365px' }" >
                </JsMindMap> -->
              </div>
            </happy-scroll>
          </div>
        </div>
        <div v-if="menuTitile === '打开'">
          <h1 style="margin-top: 0px; margin-left: 30px; font-size: 30px">
            {{ openType ? '最近打开思维导图' : '思维导图列表' }}
          </h1>
          <div style="margin-top: 20px; margin-left: 40px; font-size: 14px; height: calc(50vh)">
            <happy-scroll size="5" resize>
              <div v-if="openType">
                <div v-for="(jsmind, index) in operationJsminds" :key="index" @click="recentlyOpen(jsmind.id)">
                  <a> <a-icon type="fork" />{{ jsmind.mindMapName }}</a>
                </div>
              </div>
              <div v-else>
                <div v-for="(jsmind, index) in jsminds" :key="index" @click="recentlyOpen(jsmind.id)">
                  <a> <a-icon type="fork" />{{ jsmind.mindMapName }}</a>
                </div>
              </div>
            </happy-scroll>
          </div>
          <a-upload :file-list="fileList" name="file" :showUploadList="false" :before-upload="inputFile">
            <div style="margin-top: 15px; margin-left: 15px; cursor: pointer">
              <a-row type="flex" justify="space-between" align="middle">
                <a-col
                  :span="4"
                ><i class="glyphicon glyphicon-open icon"></i>
                  <localFileIcon :width="'100%'" :height="'30px'" alt="word" />
                </a-col>
                <a-col :span="20">
                  <div style="font-size: 20px">打开本地思维导图文件</div>
                </a-col>
              </a-row>
            </div>
          </a-upload>
          <!--  v-if="!openType" -->
          <div v-if="!openType" style="margin-top: 15px; margin-left: 15px; cursor: pointer" @click="openType = !openType" >
            <a-row type="flex" justify="space-between" align="middle">
              <a-col
                :span="4"
              ><i class="glyphicon glyphicon-open icon"></i>
                <recentOperationIcon :width="'100%'" :height="'40px'" alt="word" />
              </a-col>
              <a-col :span="20">
                <div style="font-size: 20px">打开最近操作思维导图列表</div>
              </a-col>
            </a-row>
          </div>
          <div v-else style="margin-top: 15px; margin-left: 15px; cursor: pointer" @click="openJsmindList">
            <a-row type="flex" justify="space-between" align="middle">
              <a-col
                :span="4"
              ><i class="glyphicon glyphicon-open icon"></i>
                <serveJsmindIcon :width="'100%'" :height="'35px'" alt="word" />
              </a-col>
              <a-col :span="20">
                <div style="font-size: 20px">打开服务思维导图列表</div>
              </a-col>
            </a-row>
          </div>
          <!-- <div style="margin-top: 15px; margin-left: 15px; cursor: pointer" @click="openJsmindList">
            <a-row type="flex" justify="space-between" align="middle">
              <a-col
                :span="4"
              ><i class="glyphicon glyphicon-open icon"></i>
                <serveJsmindIcon :width="'100%'" :height="'35px'" alt="word" />
              </a-col>
              <a-col :span="20">
                <div style="font-size: 20px">打开服务思维模板列表</div>
              </a-col>
            </a-row>
          </div> -->
        </div>
        <div v-if="menuTitile === '保存'">
          <div style="margin-top: 50px" class="icon-style" @click="addMindMap">
            <a-row type="flex" justify="space-between" align="middle">
              <a-col
                :span="4"
              ><i class="glyphicon glyphicon-open icon"></i>
                <copyIcon :width="'100%'" :height="'35px'" alt="word" />
              </a-col>
              <a-col :span="20">
                <div style="font-size: 20px">另存为</div>
              </a-col>
            </a-row>
          </div>
          <div class="icon-style" @click="savaMindMap">
            <a-row type="flex" justify="space-between" align="middle">
              <a-col
                :span="4"
              ><i class="glyphicon glyphicon-open icon"></i>
                <saveMindMapIcon :width="'100%'" :height="'50px'" alt="word" />
              </a-col>
              <a-col :span="20">
                <div style="font-size: 20px">保存</div>
              </a-col>
            </a-row>
          </div>
          <div class="icon-style" @click="rename">
            <a-row type="flex" justify="space-between" align="middle">
              <a-col
                :span="4"
              ><i class="glyphicon glyphicon-open icon"></i>
                <renameIcon :width="'100%'" :height="'35px'" alt="word" />
              </a-col>
              <a-col :span="20">
                <div style="font-size: 20px">重命名</div>
              </a-col>
            </a-row>
          </div>
          <div class="icon-style" @click="outputFile">
            <a-row type="flex" justify="space-between" align="middle">
              <a-col
                :span="4"
              ><i class="glyphicon glyphicon-open icon"></i>
                <jsmindOutputIcon :width="'100%'" :height="'35px'" alt="word" />
              </a-col>
              <a-col :span="20">
                <div style="font-size: 20px">导出</div>
              </a-col>
            </a-row>
          </div>
        </div>
      </a-col>
    </a-drawer>

    <!-- 查看思维导图模板 -->
    <a-modal
      :width=" '80%' "
      style="top: 0px; padding-bottom: 0px"
      :bodyStyle="{ height: 'calc(100vh - 55px)' }"
      :dialogStyle="{ top: '0px', height: '100%' }"
      centered
      :footer="null"
      title="查看模板"
      v-if="seeTemplateVisible"
      @ok="seeTemplateVisible = false"
      @cancel="seeTemplateVisible = false"
      :visible="seeTemplateVisible" >
      <JsMindMap
        :ref="currtemplate.id"
        :jsMindId="currtemplate.id"
        :jsMindMapData="currtemplate"
        :styleCss="{ width: '100%', height: 'calc(100vh - 155px)' }" >
      </JsMindMap>
    </a-modal>
    <!-- 重命名 -->
    <a-modal v-model="renameVisible" :title="modelTitle" @ok="renameWithSaveOrUpdate">
      <a-input v-model="newName" placeholder="请输入新名称" />
    </a-modal>
    <!-- 标签挂载 -->
    <a-modal
      :width=" '80%' "
      style="top: 0px; padding-bottom: 0px"
      :bodyStyle="{ height: 'calc(100vh - 55px)', padding: '0px' }"
      :dialogStyle="{ top: '0px', height: '100%' }"
      centered
      :footer="null"
      title="标签挂载"
      @ok="tagVisible = false"
      @cancel="tagVisible = false"
      v-if="tagVisible"
      :visible="tagVisible" >
      <DocModule
        :showMenus="['重置','挂载', '下载', '删除', '收藏']"
        :showColumns="['文件名称','文件类型','标签','管理模式','笔记','上传时间','进度','评分','操作']"
        :isMountedFiles="isMountedFiles"
        tagMountFlag="tagMountFlag"
        @tagMount="tagMount"></DocModule>
      <!-- <a-row>
        <a-col :span="24" >
          <a-form layout="inline" >
            <a-form-item label="标签选择：">
              <a-select
                v-model="tagForm.tagId"
                placeholder="标签选择"
                style="width: 240px" >
                <a-select-option v-for="tag in tags" :key="String(tag.tagId)"> {{ tag.tagName }} </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="项目筛选条件：">
              <a-select style="width: 150px" v-model="tagForm.projectId" >
                <a-select-option v-for="project in projects" :key="String(project.projectId)" :value="project.projectId"> {{ project.projectName }} </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="文件类型：">
              <a-select
                mode="tags"
                v-model="tagForm.fileTypes"
                placeholder="文件类型选择"
                style="width: 240px" >
                <a-select-option v-for="(fileType) in fileTypes" :key="fileType"> {{ fileType }} </a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item >
              <a-button type="primary" @click="selectFile()" >查询</a-button>
              <a-button type="primary" style="margin-left:10px" @click="tagMount()" >挂载</a-button>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-table
        :scroll="{ y: 'calc(100vh - 346px)' }"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        :columns="fileColumns"
        :data-source="fileInfos"
        :pagination="pagination"
        class="components-table-demo-nested">
        <a slot="operation" slot-scope="text">{{ text }}</a>
        <span slot="customTitle"><a-icon type="smile-o" /> Name</span>
        <span slot="tags" slot-scope="tagList">
          <a-tag
            v-for="tag in tagList"
            :key="tag"
            :color="tag === 'loser' ? 'volcano' : tag.length > 5 ? 'geekblue' : 'green'">
            {{ tag.toUpperCase() }}
          </a-tag>
        </span>
        <span slot="boolvalue" slot-scope="text">{{ isMountFile(text) }}</span>
      </a-table> -->
    </a-modal>

    <!-- 挂载弹框 -->
    <a-modal
      :width="mountType == 2 ? '80%' : '30%'"
      style="top: 0px; padding-bottom: 0px"
      :bodyStyle="mountType == 2 ? { height: 'calc(100vh - 55px)', padding: '0px' } : {}"
      :dialogStyle="{ top: '0px', height: '100%' }"
      centered
      :footer="null"
      :title="mountType == 2 ? '已有文件挂载' : mountType == 0 ? '外链挂载' : '上传挂载'"
      v-if="fileListVisible"
      @ok="fileListVisible = false"
      @cancel="fileListVisible = false"
      :visible="fileListVisible">
      <!-- 又有文件列表 -->
      <DocModule
        v-if="fileListVisible && mountType == 2"
        :showMenus="showMenus"
        :selectNodeAndJm="selectNodeAndJm"
        :selectFileIds="selectFileIds"
        :isMountedFiles="isMountedFiles"
        @mountExistSelectFiles="mountExistSelectFiles"></DocModule>
      <!-- <Files
        v-if="fileListVisible && mountType == 2"
        :styleCss="{ width: '100%', height: 'calc(100vh - 168px)' }"
        :selectFileIds="selectFileIds"
        @mountExistSelectFiles="mountExistSelectFiles"
      ></Files> -->
      <!-- 外链 -->
      <div v-if="fileListVisible && mountType == 0">
        <a-textarea
          style="background: #ffffff; font-color: #000000"
          v-model="filePaths"
          placeholder="请输入本地文件路径或目录，多条换行输入..."
          :auto-size="{ minRows: 2, maxRows: 6 }"
        />
        <a-button block type="primary" @click="fileChainMount"> 开始挂载 </a-button>
      </div>
      <!-- 上传 -->
      <a-form-model
        v-if="fileListVisible && mountType == 1"
        layout="horizontal"
        v-bind="{ labelCol: { span: 4 }, wrapperCol: { span: 14 } }">
        <a-card style="width: 100%">
          <div>
            <a-upload-dragger
              name="file"
              :file-list="fileList"
              :multiple="true"
              :showUploadList="false"
              :before-upload="beforeUpload">
              <p class="ant-upload-drag-icon"><a-icon type="inbox" /></p>
              <p class="ant-upload-text">单击或拖动文件到此区域上载</p>
            </a-upload-dragger>
            <a-button block type="primary" :disabled="fileList.length === 0" :loading="uploading" @click="handleUpload">
              {{ uploading ? '上传中' : '开始导入' }}
            </a-button>
          </div>
        </a-card>
      </a-form-model>
    </a-modal>

    <!-- 思维导图关联 -->
    <a-modal
      :width=" '50%' "
      style="top: 0px; padding-bottom: 0px"
      :bodyStyle=" { height: 'calc(100vh - 55px)' }"
      :dialogStyle="{ top: '0px', height: '100%' }"
      centered
      :footer="null"
      title="思维导图关联"
      @ok="associatedVisible = false"
      @cancel="associatedVisible = false"
      :visible="associatedVisible" >
      <!-- <div style="height:calc(100vh - 55px);">
        <happy-scroll size="5" resize >
          <div class="con">
            <div
              v-for="(item,index) in projectAndFileCount"
              :key="index"
              style="font-size:16px"
              class="text-overflow"
              :title="item.projectName + '('+item.fileCount+')'">
              {{ item.projectName }} {{ '('+item.fileCount+')' }}
            </div>
          </div>
        </happy-scroll>
      </div> -->
      <happy-scroll :min-length-h="0.2">
        <div v-if="associatedData">
          <a-row>
            <a-col :span="9">名称</a-col>
            <a-col :span="7">关联项目</a-col>
            <a-col :span="7">关联维度</a-col>
            <a-col :span="1">操作</a-col>
          </a-row>
          <a-divider></a-divider>
          <div v-for="(item,index) in associatedData.jsmindAssociatedVos" :key="index">
            <a-row style="margin-top:5px">
              <a-col :span="9">{{ item.jsmind.mindMapName }}</a-col>
              <a-col :span="7">
                <a-select
                  v-model="item.project.projectId"
                  placeholder="项目选择"
                  style="width: 200px" >
                  <a-select-option v-for="(project) in associatedData.projects" :key="project.projectId"> {{ project.projectName }} </a-select-option>
                </a-select>
              </a-col>
              <a-col :span="7">
                <a-select
                  v-model="item.dimension.dimensionId"
                  placeholder="维度选择"
                  style="width: 200px" >
                  <a-select-option v-for="(dimension) in associatedData.dimensions" :key="dimension.dimensionId"> {{ dimension.dimensionName }} </a-select-option>
                </a-select>
              </a-col>
              <a-col :span="1"> <a-button type="primary" @click="associated(item)" >关联</a-button></a-col>
            </a-row>
          </div>
        </div>
      </happy-scroll>

    </a-modal>

  </div>
</template>

<script>
import { getProjects } from '@/api/project'
// DocModule
import DocModule from '@/components/content/file/DocModule'
import Files from '@/components/content/file/Files'
import JsMindMap from '@/components/content/jsmind/jsmind'
import JsMindSimple from '@/components/content/jsmind/jsMindSimple.vue'
import {
  tagIconIcon, lineIcon, recycleBinIcon, lineColorIcon, fillColorIcon, shapeOfIcon, themeIcon, progressOfIcon,
  shrinkIcon, copyIcon, removeIcon, outputIcon, mountIcon, uninstallIcon, inputIcon, sameLevelIcon, childLevelIcon,
  tagGroupIcon, tagIcon, serveJsmindIcon, openIcon, closeIcon, returnIcon, saveMindMapIcon, saveTemplateIcon,
  jsmindInputIcon, jsmindOutputIcon, recentOperationIcon, lockIcon, unlockIcon, fullIcon, sideIcon, hspaceIcon,
  vspaceIcon, pspaceIcon, seeIcon, localFileIcon, renameIcon, deleteIcon
} from '@/core/icons'
import { Chrome } from 'vue-color'
import {
  getMindMapById,
  saveMindMapToTemplate,
  inputFile,
  getAllMindMap,
  addMindMap,
  updateMindMap,
  getTemplate,
  getOperationJsminds,
  checkMindMapName,
  getAssociated,
  associated,
  removeMindMapByid
} from '@/api/mindMap'
import { collectionList } from '@/api/collection'
import { getNodeAttr } from '@/api/nodeAttr'
import { getTags } from '@/api/tag'
import { fileChainMount, uploadFile, downloadFileByIds, downloadByMindMap, getFileTypes, getFileByParam } from '@/api/file'
import Vue from 'vue'
function UUID () {
  function S4 () {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
  }
  return S4() + S4() + '-' + S4() + '-' + S4() + '-' + S4() + '-' + S4() + S4() + S4()
}
var fontFamilys = [
  { key: '宋体', value: 'SimSun' },
  { key: '黑体', value: 'SimHei' },
  { key: '微软雅黑', value: 'Microsoft YaHei' },
  { key: '微软正黑体', value: 'Microsoft JhengHei' },
  { key: '新宋体', value: 'NSimSun' },
  { key: '新细明体', value: 'PMingLiU' },
  { key: '细明体', value: 'MingLiU' },
  { key: '标楷体', value: 'DFKai-SB' },
  { key: '仿宋', value: 'FangSong' },
  { key: '楷体', value: 'KaiTi' },
  { key: '仿宋_GB2312', value: 'FangSong_GB2312' },
  { key: '楷体_GB2312', value: 'KaiTi_GB2312' },
  { key: '隶书', value: 'LiSu' },
  { key: '幼圆', value: 'YouYuan' },
  { key: '华文细黑', value: 'STXihei' },
  { key: '华文楷体', value: 'STKaiti' },
  { key: '华文宋体', value: 'STSong' },
  { key: '华文中宋', value: 'STZhongsong' },
  { key: '华文仿宋', value: 'STFangsong' },
  { key: '方正舒体', value: 'FZShuTi' },
  { key: '方正姚体', value: 'FZYaoti' },
  { key: '华文彩云', value: 'STCaiyun' },
  { key: '华文琥珀', value: 'STHupo' },
  { key: '华文隶书', value: 'STLiti' },
  { key: '华文行楷', value: 'STXingkai' },
  { key: '华文新魏', value: 'STXinwei' }
]
export default {
  name: 'App',
  components: {
    deleteIcon,
    JsMindSimple,
    DocModule,
    Files,
    removeIcon,
    copyIcon,
    renameIcon,
    recentOperationIcon,
    serveJsmindIcon,
    localFileIcon,
    seeIcon,
    hspaceIcon,
    vspaceIcon,
    pspaceIcon,
    JsMindMap,
    Chrome,
    lineIcon,
    recycleBinIcon,
    lineColorIcon,
    fillColorIcon,
    shapeOfIcon,
    themeIcon,
    progressOfIcon,
    outputIcon,
    mountIcon,
    uninstallIcon,
    inputIcon,
    sameLevelIcon,
    childLevelIcon,
    tagGroupIcon,
    tagIcon,
    openIcon,
    closeIcon,
    returnIcon,
    saveMindMapIcon,
    saveTemplateIcon,
    jsmindInputIcon,
    jsmindOutputIcon,
    shrinkIcon,
    lockIcon,
    unlockIcon,
    fullIcon,
    sideIcon,
    tagIconIcon
  },
  data () {
    return {
      showMenus: ['重置', '下载', '删除', '收藏'],
      currtemplate: null,
      seeTemplateVisible: false,
      copyNodeData: null,
      associatedData: null,
      associatedVisible: false,
      menuType: ['insert', 'task', 'view'], // 菜单类型 file：文件 insert：插入 task：任务  design：设计
      selectedRowKeys: [],
      selectedRowData: null,
      fileInfos: [],
      fileTypes: [],
      projects: [],
      tagForm: {
        tagId: '',
        projectId: null,
        fileTypes: []
      },
      fileColumns: [
        { title: '文件名称', dataIndex: 'fileName', slots: { title: 'customTitle' }, scopedSlots: { customRender: 'name' } },
        { title: '进度', dataIndex: 'progressOf' },
        { title: '评分', dataIndex: 'score' }
      ],
      pagination: {
        pageNo: 1,
        pageSize: 20, // 默认每页显示数量
        showSizeChanger: true, // 显示可改变每页数量
        pageSizeOptions: ['20', '50', '100'], // 每页数量选项
        showTotal: total => `共 ${this.pagination.total} 条数据`, // 显示总数
        onShowSizeChange: this.onShowSizeChange, // 改变每页数量时更新显示
        onChange: this.changePage, // 点击页码事件
        total: 0 // 总条数
        // size: 'small' // 迷你版
       },
      tagVisible: false, // 标签弹框
      tags: [],
      imgIcons: [], // 图标静态数据
      isMountedFiles: [], // 判断是否挂载的文件（已挂载的文件）
      selectFileIds: [], // 选中的文件，选中节点或者思维导图
      newBuildMindMap: null, // 根据模板新建的思维导图
      jsmindTemplates: null, // 思维导图模板列表
      modelTitle: null,
      renameType: null,
      newName: '',
      renameVisible: false,
      openType: true, // true: 最近 false：思维导图列表
      jsminds: [],
      uploading: false,
      filePaths: null,
      mountType: null,
      fileListVisible: false,
      fontFamilys: fontFamilys, // 字体样式
      fileList: [], // 导入时文件集合
      trigger: ['click'],
      themes: [
        'default',
        'primary',
        'warning',
        'danger',
        'success',
        'info',
        'greensea',
        'nephrite',
        'belizeholle',
        'wisteria',
        'asphalt',
        'orange',
        'pumpkin',
        'pomegranate',
        'clouds',
        'asbestos'
      ],
      themeMapping: [
        { en: 'default', zn: '默认(白色)' },
        { en: 'primary', zn: '主要(深蓝色)' },
        { en: 'warning', zn: '警告(橘色)' },
        { en: 'danger', zn: '危险(红色)' },
        { en: 'success', zn: '成功(绿色)' },
        { en: 'info', zn: '信息(浅蓝色)' },
        { en: 'greensea', zn: '格林西(蓝绿色)' },
        { en: 'nephrite', zn: '软玉' },
        { en: 'default', zn: '默认的' },
        { en: 'default', zn: '默认的' },
        { en: 'default', zn: '默认的' },
        { en: 'default', zn: '默认的' },
        { en: 'default', zn: '默认的' },
        { en: 'default', zn: '默认的' },
        { en: 'default', zn: '默认的' }
      ],
      fontSize: 12,
      operationJsminds: [],
      operationList: [],
      collectionList: [],
      menuTitile: '新建', // 菜单标题
      mouseLoc: 0,
      fileMenus: ['新建', '打开', '保存'],
      fontColor: '#000', // 字体颜色
      lineColor: '#000', // 线条颜色
      backgroundColor: '#000', // 背景颜色
      lineThickness: 2, // 线条粗细
      proportion: 1, // 缩放比例
      hspace: 30, // 节点间水平距离
      vspace: 20, // 节点间垂直距离
      pspace: 13, // 电线水平距离
      mindMaps: [],
      activeKey: null,
      currSelectJsmind: null,
      editable: true,
      drawerVisible: false,
      selectNodeStatus: null, // 是否选中节点,
      selectNodeAndJm: null // 操作节点及jm操作对象
    }
  },
  computed: {
    mountSelectFiles () { // 将store中的属性变为计算属性进行监听
      return this.$store.state.mountSelectFiles
    },
    currMindMapsComputed: function () {
      // 过滤出当前显示脑图
      return this.mindMaps.filter(item => item.id === this.activeKey)
    }
  },
  mounted () {
    var openJsminds = Vue.ls.get('openJsminds', null)
    if (openJsminds != null && openJsminds.length !== 0) {
      this.mindMaps = openJsminds
      this.activeKey = openJsminds[0].id
      this.editable = openJsminds[0].editable
      this.currSelectJsmind = openJsminds[0]
      const currSelectJsmind = Vue.ls.get('currSelectJsmind', null)
      if (currSelectJsmind != null) {
        this.activeKey = currSelectJsmind.id
        this.currSelectJsmind = currSelectJsmind
      }
    }
    collectionList().then((res) => { this.collectionList = res.data })
    getNodeAttr().then(res => { this.imgIcons = res.data })
    getTags().then(res => { if (res.success) { this.tags = res.data } })
    // 获取所有项目
    getProjects().then(res => { if (res.success) { this.projects = res.data } })
    getFileTypes().then(res => { if (res.success) { this.fileTypes = res.data } })
  },
  watch: {
    mountSelectFiles (newVlaue, oldValue) {
      console.log('监听到选择挂载文件变化=》', newVlaue)
    },
    currMindMapsComputed: {
      handler (newVal) {
        // 修改 线条颜色、线条粗细、节点水平、节点垂直
        const curr = this.mindMaps.filter((item) => this.activeKey === item.id)[0]
        this.editable = curr.editable
        this.currSelectJsmind = curr
        // this.lineColor = newVal[0].data.view['line_color']
        // this.lineThickness = newVal[0].data.view['line_width']
        // this.hspace = newVal[0].data.layout['hspace']
        // this.vspace = newVal[0].data.layout['vspace']
      },
      deep: true

    },
    // 监听字体大小变化
    fontSize (newVlaue, oldValue) {
      if (this.selectNodeStatus) {
         this.$refs[this.activeKey][0].changeFontSize(newVlaue)
      } else {
        this.$message.warning('请选择节点')
      }
    },
    // 监听字体颜色改变
    fontColor (newVlaue, oldValue) {
      // 判断是否选择节点
      if (this.selectNodeStatus) {
        this.$refs[this.activeKey][0].changeFontColor(newVlaue.hex)
      } else {
        this.$message.warning('请选择节点')
      }
    },
    // 监听线条颜色变化
    lineColor (newVlaue, oldValue) {
      this.currSelectJsmind.data.view['line_color'] = this.lineColor.hex
      this.$refs[this.activeKey][0].changeContainer(this.currSelectJsmind)
    },
    // 监听节点背景颜色
    backgroundColor (newVlaue, oldValue) {
      if (this.selectNodeStatus) {
        this.currSelectJsmind.data.data['background-color'] = newVlaue.hex
        this.savaCurrSelectJsmind()
        this.$refs[this.activeKey][0].changeBackgroundColor(newVlaue.hex)
      } else {
        this.$message.warning('请选择节点')
      }
    },
    // 监听线条粗细变化
    lineThickness (newVlaue, oldValue) {
      this.currSelectJsmind.data.view['line_width'] = newVlaue
      this.$refs[this.activeKey][0].changeContainer(this.currSelectJsmind)
    },
    // 监听缩放比例
    proportion (newVlaue, oldValue) {
      this.$refs[this.activeKey][0].zoom(newVlaue)
    },
    // 监听节点间水平距离
    hspace (newVlaue, oldValue) {
      this.currSelectJsmind.data.layout['hspace'] = newVlaue
      this.$refs[this.activeKey][0].changeContainer(this.currSelectJsmind)
    },
    // 监听点线水平距离
    pspace (newVlaue, oldValue) {
      this.currSelectJsmind.data.layout['pspace'] = newVlaue
      this.$refs[this.activeKey][0].changeContainer(this.currSelectJsmind)
    },
    // 监听节点间垂直距离
    vspace (newVlaue, oldValue) {
      this.currSelectJsmind.data.layout['vspace'] = newVlaue
      this.$refs[this.activeKey][0].changeContainer(this.currSelectJsmind)
    },

    // 监听思维导图变化，改变缓存数据
    mindMaps (newVal, oldVal) {
      Vue.ls.set('openJsminds', newVal, 1000 * 60 * 60 * 24)
    },
    // 监听当前页面显示思维导图数据变化，实时保存
    // currSelectJsmind (newVal, oldVal) { Vue.ls.set('currSelectJsmind', newVal, 1000 * 60 * 60 * 24) },
    currSelectJsmind: {
      handler (newVal, oldVal) {
        Vue.ls.set('currSelectJsmind', newVal, 1000 * 60 * 60 * 24)
      },
      deep: true
    }
  },
  methods: {
    // 复选框选中记录数组
    onSelectChange (selectedRowKeys, row) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRowData = row
    },
    // 标签挂载
    tagMount (list) {
      // 获取标签名称
      list.forEach(item => {
        this.$refs[this.activeKey][0].tagMount(item.tagName, item.fileIds)
      })
      // var tag = this.tags.filter(item => String(item.tagId) === this.tagForm.tagId)[0]
      // this.$refs[this.activeKey][0].tagMount(tag.tagName, this.selectedRowKeys)
      this.tagVisible = false
    },
    selectFile () {
      getFileByParam(this.tagForm).then(res => {
        if (res.success) {
          this.fileInfos = res.data
          this.selectedRowKeys = res.data.map(item => { return item.fileId })
          }
      })
    },
    // ---------------标记开始-------------------------------
    openTag (type) {
      if (!this.selectNodeStatus) {
        this.$message.warning('请选择节点')
        return
      }
      if (type === '0') { // 标签
        this.selectFileIds = []
        this.isMountedFiles = this.$refs[this.activeKey][0].getMindMapFileIds(this.currSelectJsmind)
        this.tagVisible = true
      }
    },
    // ---------------标记结束-------------------------------

    // ---------------监听属性修改为方法开始-------------------------------

    // ---------------监听属性修改为方法结束-------------------------------
    development () {
      console.log(this.$refs[this.activeKey], this.$refs)
      alert('开发中')
    },
    // 打开思维导图列表
    openJsmindList () {
      getAllMindMap().then((res) => {
        if (res.success) {
          this.jsminds = res.data
          this.openType = !this.openType
        }
      })
    },
    // 将当前数据保存到缓存
    savaCurrSelectJsmind () {
      Vue.ls.set('currSelectJsmind', this.currSelectJsmind, 1000 * 60 * 60 * 24)
    },
    // 是否选中节点
    isSelectNode (data, selectNodeAndJm) {
      this.selectNodeStatus = data
      this.selectNodeAndJm = selectNodeAndJm
    },
    // --------------------图标开始----------------------------------
    insertIcon (data) {
      var img = `<img style="width:25px;height:25px;background-color:white;" src="${data.imageUrl}">`
      this.$refs[this.activeKey][0].insertIcon(img, data.id)
    },
    deleteIcon (type) { this.$refs[this.activeKey][0].deleteIcon(type) },
    // --------------------图标结束----------------------------------
    // --------------------任务开始----------------------------------
    downloadFileUtil (fileName, res) {
          const content = res
          const blob = new Blob([content])
          if ('download' in document.createElement('a')) {
          // 非IE下载
            const elink = document.createElement('a')
            elink.download = fileName
            elink.style.display = 'none'
            elink.href = URL.createObjectURL(blob)
            document.body.appendChild(elink)
            elink.click()
            URL.revokeObjectURL(elink.href) // 释放URL 对象
            document.body.removeChild(elink)
          } else {
          // IE10+下载
            navigator.msSaveBlob(blob, fileName)
          }
        },
    // 下载
    download (type) {
      // 获取当前节点 getCurrNode
      var currJsmind = this.$refs[this.activeKey][0].getCurrNode()
      currJsmind = JSON.parse(JSON.stringify(currJsmind))
      // currNode.fileIds = currNode.data.fileIds
      // 删除parent，消除循环引用序列化问题
      // delete currNode.parent
      // 思维导图
      if (type === 0) {
        downloadByMindMap(this.currSelectJsmind).then(res => {
          this.downloadFileUtil(this.currSelectJsmind.mindMapName + '.zip', res)
        })
      }
      // 该节点及其子孙文件
      if (type === 1) {
        if (currJsmind == null) {
          this.$message.warning('请选择节点')
          return
        }

        // 组织该节点为jismind对象
        downloadByMindMap(currJsmind).then(res => {
          this.downloadFileUtil(currJsmind.data.data.topic + '.zip', res)
        })
      }
      // 该节点文件
      if (type === 2) {
        if (currJsmind == null) {
          this.$message.warning('请选择节点')
          return
        }
        // 获取选中文件节点
        const fileIds = currJsmind.data.data['fileIds']
        if (fileIds === undefined || fileIds.length === 0) {
          this.$message.warning('选择节点未挂载文件')
          return
        }
        downloadFileByIds(JSON.stringify({ 'ids': fileIds })).then(res => {
          this.downloadFileUtil(currJsmind.data.data.topic + '.zip', res)
        })
      }
      // 指定为文件
      if (type === 3) {}
    },
    // 查看
    see (type) {
      this.showMenus = ['重置', '下载', '删除', '收藏']
      // 清空状态
      this.selectFileIds = []
      this.isMountedFiles = this.$refs[this.activeKey][0].getMindMapFileIds(this.currSelectJsmind)
      switch (type) {
        case 0: // 查看全部文件
          this.selectFileIds = null; break
        case 1: // 查看当前节点
          if (!this.selectNodeStatus) { this.$message.warning('请选择节点'); return }
          this.selectFileIds = this.$refs[this.activeKey][0].getSelectNodeFileIds(); break
        case 2: // 查看当前思维全部文件
          this.selectFileIds = this.$refs[this.activeKey][0].getMindMapFileIds(this.currSelectJsmind); break
      }
      this.mountType = 2
      this.fileListVisible = true
    },
    // 卸载文件
    unmount (type) {
      if (type === 3) {
        this.$refs[this.activeKey][0].unMountFilesByMindMap()
        return
      }
      // 卸载指定文件
      if (type === 0) {
          this.$refs[this.activeKey][0].showDrawer()
          return
      }
      if (this.selectNodeStatus) {
        if (type === 1) { this.$refs[this.activeKey][0].unMountFilesByNode() }
        if (type === 2) { this.$refs[this.activeKey][0].unMountFilesBychildrenNode() }
      } else { this.$message.warning('请选择节点') }
    },
    beforeUpload (file) {
      this.fileList = [...this.fileList, file]
      return false
    },
    // 文件上传
    handleUpload () {
      this.showUploadList = true
      const { fileList } = this
      const formData = new FormData()
      fileList.forEach((file) => {
        formData.append('files', file)
      })
      this.uploading = true
      uploadFile(formData)
        .then((res) => {
          if (res.success) {
            const fileIds = res.data.success.map((item) => {
              return item.fileId
            })
            // 挂载
            this.$refs[this.activeKey][0].mountExistSelectFiles(fileIds)
            // 保存
            this.savaMindMap()
            // 关闭弹框
            this.fileListVisible = false
          }
        })
        .catch((error) => {
          this.$message.error('上传异常', error)
        }).finally((o) => {
          this.fileList = []
          this.uploading = false
          this.showUploadList = false
        })
    },
    // 外链挂载
    fileChainMount () {
      fileChainMount(JSON.stringify({ paths: this.filePaths })).then((res) => {
        const fileIds = res.data.map((item) => {
          return item.fileId
        })
        // 挂载
        this.$refs[this.activeKey][0].mountExistSelectFiles(fileIds)
        // 保存
        this.savaMindMap()
        // 关闭弹框
        this.fileListVisible = false
      })
    },
    mount (type) {
      if (this.selectNodeStatus) {
        this.mountType = type
        this.fileListVisible = true
        if (type === 2) { this.showMenus = ['重置', '挂载', '下载', '删除', '收藏'] }
        // 获取当前节点挂载文件id集合
      } else {
        this.$message.warning('请选择节点')
      }
    },
    // 挂载已有选中文件
    mountExistSelectFiles (fileIds) {
      // 调用思维导图组件的挂方法
      this.$refs[this.activeKey][0].mountExistSelectFiles(fileIds)
      // 关闭当前弹框
      this.$message.success('挂载成功')
      // this.fileListVisible = false
    },
    // --------------------任务结束----------------------------------
    // --------------------导入导出开始-----------------------
    // 关闭思维导图
    closeJsmind (type) {
      if (type === 0) {
        // 关闭当前页面
        this.remove(this.currSelectJsmind.id)
      } else if (type === 1) {
        // 关闭右侧
        this.removeRightOrLeft(this.currSelectJsmind.id, 'right')
      } else if (type === 2) {
        // 关闭左侧
        this.removeRightOrLeft(this.currSelectJsmind.id, 'left')
      }
    },
    outputFile () {
      this.$refs[this.activeKey][0].outputFile()
    },
    inputFile (file) {
      this.fileList = [...this.fileList, file]
      const { fileList } = this
      const formData = new FormData()
      fileList.forEach((file) => {
        formData.append('files', file)
      })
      inputFile(formData)
        .then((res) => {
          if (res.success) {
            // 判断当前开始思维导图是否以及代开
            for (var i = 0; i < this.mindMaps.length; i++) {
              if (this.mindMaps[i].id === res.data.id) {
                this.$message.warning('当前打开思维导图已打开')
                return
              }
            }
            this.mindMaps.push(res.data)
            this.activeKey = res.data.id
            this.currSelectJsmind = res.data
          }
        })
        .catch((error) => {
          this.$message.error('上传异常', error)
        })
        .finally((o) => {
          this.fileList = []
        })
    },
    // --------------------导入导出结束-----------------------
    // --------------------样式修改开始-----------------------
    // 字体样式
    changeFamily (value) {
      this.$refs[this.activeKey][0].changeFamily(value.key)
    },
    // 字体大小
    changeFontSize (value) {
      this.$refs[this.activeKey][0].changeFontSize(value.key)
    },
    // 粗细
    toggleFontThickness () {
      var fontWeight = this.currSelectJsmind.data.data['font-weight']
      if (fontWeight === undefined || fontWeight === 'normal') {
        // 当前为正常，转化为加粗
        this.currSelectJsmind.data.data['font-weight'] = 'bold'
      } else {
        this.currSelectJsmind.data.data['font-weight'] = 'normal'
      }
      this.savaCurrSelectJsmind()
      this.$refs[this.activeKey][0].toggleFontThickness()
    },
    // 斜体切换
    toggleFontItalic () {
      this.$refs[this.activeKey][0].toggleFontItalic()
    },
    // 下划线切换
    toggleUnderline () {
      this.$refs[this.activeKey][0].toggleUnderline()
    },
    // 中划线
    toggleLineThrough () {
      this.$refs[this.activeKey][0].toggleLineThrough()
    },
    // --------------------样式修改结束------------------------------

    // --------------------详图开始------------------------------
    // 展开至
    expandToDepth (depth) {
      this.$refs[this.activeKey][0].expandToDepth(depth)
    },
    // 展开子节点
    expandNode () {
      this.$refs[this.activeKey][0].expandNode()
    },
    // 展开全部子节点
    expandAllNode () {
      this.$refs[this.activeKey][0].expandAllNode()
    },
    // 收缩
    toggleNode () {
      this.$refs[this.activeKey][0].toggleNode()
    },
    // 收起子节点
    collapseNode () {
      this.$refs[this.activeKey][0].collapseNode()
    },
    // 放大
    zoomIn () {
      this.$refs[this.activeKey][0].zoomIn()
    },
    // 缩小
    zoomOut () {
      this.$refs[this.activeKey][0].zoomOut()
    },
    // --------------------详图结束------------------------------

    // --------------------节点编辑开始------------------------------
    toggleIsEditable (editable) {
      this.$refs[this.activeKey][0].toggleIsEditable(editable)
      this.currSelectJsmind.editable = editable
    },

    toggleMode (mode) {
      this.$refs[this.activeKey][0].toggleMode(mode)
      this.currSelectJsmind.mode = mode
      // 将当前打开思维导图id保存到内存
      // Vue.ls.set('currSelectJsmind', this.currSelectJsmind)
      // 重新加载当前思维导图
      var newMindMaps = []
      this.mindMaps.forEach((item) => {
        if (this.currSelectJsmind.id === item.id) {
          item.mode = mode
        }
        newMindMaps.push(item)
      })
      this.mindMaps = newMindMaps
      // 刷新当前页面，缺点：有短暂刷新空白，用户体验不好
      // this.$router.go(0)
      // 刷新子组件容器
      this.$refs[this.activeKey][0].changeContainer(this.currSelectJsmind)
    },

    // --------------------节点编辑结束------------------------------

    // --------------------添加主题开始------------------------------
    // 删除主题
    removeNode () {
      this.$refs[this.activeKey][0].removeNode()
    },
    // 添加主题
    addNode (type) {
      if (type === 0) {
        this.$refs[this.activeKey][0].addNode()
      } // 添加子主题
      if (type === 1) {
        this.$refs[this.activeKey][0].addNodeBefore()
      } // 添加同级主题 - 前
      if (type === 2) {
        this.$refs[this.activeKey][0].addNodeAfter()
      } // 添加同级主题 - 后
    },

    // --------------------添加主题结束------------------------------

    // --------------------对象格式开始------------------------------
    changeTheme (theme) {
      this.currSelectJsmind.theme = theme
      this.savaCurrSelectJsmind()
      this.$refs[this.activeKey][0].changeTheme(theme)
    },
    // changeLineColor () {
    //   this.showFontColor = true
    // },
    // --------------------对象格式结束------------------------------

    // --------------------CRUD开始------------------------------
    saveMindMapToTemplate () {
      this.modelTitle = '模板名称'
      this.renameType = 'saveMindMapToTemplate'
      this.newName = ''
      this.renameVisible = true
    },
    newBuildMindMapBefore (newBuildMindMap) {
      this.newBuildMindMap = newBuildMindMap
      this.modelTitle = '思维导图名称'
      this.renameType = 'newBuildMindMapByTemplate'
      this.newName = ''
      this.renameVisible = true
    },
    // 添加tab标签页面（添加思维导图）
    add () {
      this.modelTitle = '思维导图名称'
      this.renameType = 'addNewMindMap'
      this.newName = ''
      this.renameVisible = true
    },
    // 关闭标签页（关闭思维导图） targetKey为当前选中的key
    remove (targetKey) {
      // 弹框提示是否删除当前思维导图
      const vm = this
      this.$confirm({
        title: '是否关闭当前思维导图?',
        content: '确定关闭，取消不关闭',
        onOk () {
          const mindMaps = vm.mindMaps.filter((pane) => pane.id !== targetKey)
          vm.mindMaps = mindMaps
          if (vm.mindMaps != null && vm.mindMaps.length > 0) {
            vm.activeKey = vm.mindMaps[0].id
            vm.currSelectJsmind = vm.mindMaps[0]
          } else {
            vm.activeKey = null
          }
        },
        onCancel () {
          vm.$message.success('取消关闭')
        }
      })
    },
    // 关闭右侧
    removeRightOrLeft (targetKey, type) {
      // 弹框提示是否删除当前思维导图
      var title
      if (type === 'right') {
        title = '是否关闭右侧思维导图?'
      } else if (type === 'left') {
        title = '是否关闭左侧思维导图?'
      }
      const vm = this
      this.$confirm({
        title: title,
        content: '确定关闭，取消不关闭',
        onOk () {
          // 查询当前思维导图索引位置
          var index
          for (var i = 0; i < vm.mindMaps.length; i++) {
            if (vm.mindMaps[i].id === targetKey) {
              index = i
              break
            }
          }
          var newMindMaps = []
          for (var j = 0; j < vm.mindMaps.length; j++) {
            if (type === 'right') {
              if (j <= index) {
                newMindMaps.push(vm.mindMaps[j])
              }
            } else if (type === 'left') {
              if (j >= index) {
                newMindMaps.push(vm.mindMaps[j])
              }
            }
          }
          vm.mindMaps = newMindMaps
          // if (vm.mindMaps != null && vm.mindMaps.length > 0) {
          //   vm.activeKey = vm.mindMaps[0].id
          //   vm.currSelectJsmind = vm.mindMaps[0]
          // } else { vm.activeKey = null }
        },
        onCancel () {
          vm.$message.success('取消关闭')
        }
      })
    },
    rename () {
      this.renameType = 'renameWithUpdate'
      this.modelTitle = '重命名'
      this.newName = this.currSelectJsmind.mindMapName
      this.renameVisible = true
    },
    // 添加新的空思维导图
    addNewMindMap () {
        const data = {
          meta: { name: this.newName, '': '', version: '' },
          format: 'node_tree',
          data: { id: 'root', topic: '中心主题', expanded: true }
        }
        const id = UUID()
        const newMindMap = {
          id: id,
          data: data,
          mindMapName: this.newName,
          format: 'node_tree',
          mode: 'full',
          editable: 1,
          isTemplate: 0,
          supportHtml: 1
        }
        this.mindMaps.push(newMindMap)
        this.currSelectJsmind = newMindMap
        // tab选项卡切换到该思维导图
        this.activeKey = id
    },
    // 重命名
    renameWithSaveOrUpdate () {
      // 校验当前思维导图名称是否存在
      checkMindMapName(this.newName).then(res => {
        if (res.success) {
          if (res.data) {
            this.$message.warning('名称已存在，请重新输入')
            return
          }
          if (this.renameType === 'saveMindMapToTemplate') {
              saveMindMapToTemplate(this.currSelectJsmind, this.newName).then((res) => {
                res.success ? this.$message.success('模板保存成功') : this.$message.error('模板保存失败')
              })
          } else if (this.renameType === 'newBuildMindMapByTemplate') {
            this.drawerVisible = false
            if (this.newBuildMindMap == null) { // 新建空思维导图
              this.addNewMindMap()
            } else { // 根据模板新建思维导图
              // 修改模板名称信息
              var timestamp = new Date().getTime()
              const newId = 'T_' + UUID() + ':' + timestamp
              this.newBuildMindMap.id = newId
              this.newBuildMindMap.mindMapName = this.newName
              this.newBuildMindMap.data.meta.name = this.newName
              this.newBuildMindMap.isTemplate = 0
              // 删除对象中的属性
              delete this.newBuildMindMap.dimensionId
              delete this.newBuildMindMap.projectId
              this.mindMaps.push(this.newBuildMindMap)
              this.currSelectJsmind = this.newBuildMindMap
              this.activeKey = newId
            }
          } else {
            // 深克隆数据
            var data = JSON.parse(JSON.stringify(this.currSelectJsmind))
            data.mindMapName = this.newName
            data.data.meta.name = this.newName
            if (this.renameType === 'renameWithSave') {
              data.id = null
              addMindMap(data).then((res) => {
                if (res.success) { this.$message.success('另存成功') }
              })
            }
            if (this.renameType === 'renameWithUpdate') {
              updateMindMap(data).then((res) => {
                if (res.success) { this.$message.success('重命名成功') }
              })
            }
            if (this.renameType === 'addNewMindMap') { this.addNewMindMap() }
          }
          this.renameVisible = false
        }
      })
    },
    // 另存为
    addMindMap () {
      // 修改思维导图，名称
      this.renameType = 'renameWithSave'
      this.modelTitle = '重命名'
      this.renameVisible = true
    },
    // 删除模板
    editTemplate (template) {
      if (this.mindMaps.map((item) => item.id).indexOf(template.id) !== -1) {
        this.$message.warning(template.mindMapName + '已打开')
        return
      }
      this.mindMaps.push(template)
      this.activeKey = template.id
    },
    deleteTemplate (template) {
      const vm = this
      this.$confirm({
        title: '是否删除当前思维导图模板?',
        content: '确定删除，取消不删除',
        onOk () {
          removeMindMapByid(template.id).then(res => {
            if (res.success) {
              const jsmindTemplates = vm.jsmindTemplates.filter((pane) => pane.id !== template.id)
              vm.jsmindTemplates = jsmindTemplates
              vm.$message.success('删除成功')
            }
          })
        },
        onCancel () {
          vm.$message.success('取消删除')
        }
      })
    },
    // 删除思维导图或思维导图模板
    deleteMindMap () {
       // 关闭当前页面
      const vm = this
      this.$confirm({
        title: '是否删除当前思维导图?',
        content: '确定删除，取消不删除',
        onOk () {
          removeMindMapByid(vm.currSelectJsmind.id).then(res => {
            if (res.success) {
              const mindMaps = vm.mindMaps.filter((pane) => pane.id !== vm.currSelectJsmind.id)
              vm.mindMaps = mindMaps
              if (vm.mindMaps != null && vm.mindMaps.length > 0) {
                vm.activeKey = vm.mindMaps[0].id
                vm.currSelectJsmind = vm.mindMaps[0]
              } else {
                vm.activeKey = null
              }
              vm.$message.success('删除成功')
            }
          })
        },
        onCancel () {
          vm.$message.success('取消删除')
        }
      })
    },
    // 保存思维导图
    savaMindMap () {
      // 调用子组件更新方法
      this.$refs[this.activeKey][0].savaMindMap()
    },
    recentlyOpen (targetId) {
      // 打开思维导图
      getMindMapById(targetId).then((res) => {
        if (res.success) {
          // 判断当前 打开思维导图是否已打开
          if (this.mindMaps.map((item) => item.id).indexOf(res.data.id) !== -1) {
            this.$message.warning(res.data.mindMapName + '已打开')
            return
          }
          this.mindMaps.push(res.data)
          this.currSelectJsmind = res.data
          this.activeKey = res.data.id
        }
      })
    },
    // --------------------CRUD结束------------------------------

    importLocJsmind () {
      alert('测试')
    },
    enterFun (index) {
      this.mouseLoc = index
    },
    clickMenu (fileMenu) {
      this.menuTitile = fileMenu

      // alert(fileMenu)
    },

    handleChange (value) {
    },
    associated (data) {
      var ids = { 'dimensionId': data.dimension.dimensionId, 'jsmindId': data.jsmind.id, 'projectId': data.project.projectId }
      associated(ids).then(res => {
        res.success ? this.$message.success('关联成功') : this.$message.error('关联失败')
      })
    },
    menu (type) {
      if (type === 'associated') {
        // 获取关联数据
        getAssociated().then(res => {
          if (res.success) {
            this.associatedData = res.data
            this.associatedVisible = true
           }
        })
      } else if (type === 'all') {
        this.menuType = ['insert', 'task', 'view']
      } else if (type === 'file') {
        // 初始化抽屉数据
        Promise.all([
          new Promise((resolve, reject) => {
            getOperationJsminds().then((res) => {
              if (res.success) {
                resolve(res)
              }
            }) // 获取最近操作思维导图 // this.operationJsminds = res.data
          }),
          new Promise((resolve, reject) => {
            getTemplate().then((res) => {
              if (res.success) {
                resolve(res)
              }
            }) // 获取思维导图模板
          })
        ]).then((res) => {
          this.operationJsminds = res[0].data
          this.jsmindTemplates = res[1].data
          this.drawerVisible = true
        }) // 是一个数组，里边的res[index]值就是两个不同的请求返回的结果
      } else {
        this.menuType = [type]
      }
    },
    onEdit (targetKey, action) {
      this[action](targetKey)
    }
    // changeMountStatus () {
    //   this.selectedRowKeys = []
    // },
  }
}
</script>

<style>
.icon-style {
  margin-top: 15px;
  margin-left: 30px;
  cursor: pointer;
}
.font-style {
  font-size: 25px;
  margin-left: 25px;
  font-weight: bold;
  cursor: pointer;
}
#minder-container {
  height: 100vh;
}
.button-menu {
  font-size: 22px;
}
.display-line {
  display: inline-block;
  margin-left: 10px;
}
.shortcut-menu {
  /* background: #424a58 */
  background: #ccd2dd;
}
.mouseLoc {
  background: #e6cf07;
}

</style>

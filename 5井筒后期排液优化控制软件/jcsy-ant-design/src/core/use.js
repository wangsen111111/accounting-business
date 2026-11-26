import Vue from 'vue'
import VueStorage from 'vue-ls'
import config from '@/config/defaultSettings'

// base library
import Antd from 'ant-design-vue'
import Viser from 'viser-vue'
import VueCropper from 'vue-cropper'
import 'ant-design-vue/dist/antd.less'

// ext library
import VueClipboard from 'vue-clipboard2'
import MultiTab from '@/components/MultiTab'
import PageLoading from '@/components/PageLoading'
import PermissionHelper from '@/utils/helper/permission'
// import '@/components/use'
import './directives/action'
import {
    Compact,
    Grayscale,
    Material,
    Slider,
    Swatches,
    Photoshop,
    Sketch,
    Chrome,
    Alpha,
    Checkboard,
    EditableInput,
    Hue,
    Saturation,
    ColorMixin } from 'vue-color'
import echarts from 'echarts'
// import HappyScroll from 'vue-happy-scroll'

import infiniteScroll from 'vue-infinite-scroll'
import { RecycleScroller } from 'vue-virtual-scroller'
import contentmenu from 'v-contextmenu'
import mammoth from 'mammoth'
import pdf from 'vue-pdf'
import kityMinder from 'vue-kityminder-editor'
import vuedraggable from 'vuedraggable'
import VueDraggableResizable from 'vue-draggable-resizable'

VueClipboard.config.autoSetContainer = true

Vue.use(Antd)
Vue.use(Viser)
Vue.use(MultiTab)
Vue.use(PageLoading)
Vue.use(VueStorage, config.storageOptions)
Vue.use(VueClipboard)
Vue.use(PermissionHelper)
Vue.use(VueCropper)

Vue.use(Compact)
Vue.use(Grayscale)
Vue.use(Material)
Vue.use(Slider)
Vue.use(Swatches)
Vue.use(Sketch)
Vue.use(Photoshop)

Vue.use(Chrome)
Vue.use(Alpha)
Vue.use(Checkboard)
Vue.use(EditableInput)
Vue.use(Hue)
Vue.use(Saturation)
Vue.use(ColorMixin)
Vue.use(echarts)
// Vue.use(HappyScroll)
Vue.use(infiniteScroll)
Vue.use(RecycleScroller)
Vue.use(contentmenu)
Vue.use(mammoth)
Vue.use(pdf)
Vue.use(vuedraggable)
Vue.use(VueDraggableResizable)
Vue.use(kityMinder)

process.env.NODE_ENV !== 'production' && console.warn('[antd-pro] WARNING: Antd now use fulled imported.')

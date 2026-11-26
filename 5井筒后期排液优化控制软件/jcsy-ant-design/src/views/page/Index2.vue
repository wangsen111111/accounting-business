
<template>
  <div id="layout">
    <div id="jsmind_nav">
      <div>1. Open</div>
      <ol type="A">
        <li><button @click="open_json();">open example</button></li>
        <li><button @click="open_ajax();">open remote</button></li>
        <li><button onclick="prompt_info('see 6.Multi Format');">open local file</button></li>
        <li><button onclick="prompt_info('see 6.Multi Format');">save local file</button></li>
        <li><button onclick="screen_shot();">screenshot</button></li>
      </ol>
      <div>2. Select &amp; Toggle</div>
      <ol type="A">
        <li><button onclick="select_node();">select a node</button></li>
        <li><button onclick="prompt_info('please try click a node');">try click a node</button></li>
        <li><button onclick="show_selected();">get the selected</button></li>
      </ol>
      <div>3. Edit</div>
      <ol type="A">
        <li><button onclick="toggle_editable(this);">disable editable</button></li>
        <li><button onclick="add_node();">add a node</button></li>
        <li><button onclick="add_image_node();">add a image node</button></li>
        <li><button onclick="modify_node();">modify node</button></li>
        <li><button onclick="prompt_info('please try double click a node');">try double click</button></li>
        <li><button onclick="move_node();">move a node</button></li>
        <li><button onclick="move_to_first();">move to first</button></li>
        <li><button onclick="move_to_last();">move to last</button></li>
        <li><button onclick="remove_node();">remove node</button></li>
      </ol>
      <div>4. Style</div>
      <ol type="A">
        <li><button onclick="change_text_font();">change font</button></li>
        <li><button onclick="change_text_color();">change color</button></li>
        <li><button onclick="change_background_color();">change bg-color</button></li>
        <li><button onclick="change_background_image();">change background</button></li>
      </ol>
      <div>5. Theme</div>
      <ol type="A">
        <li>
          <select onchange="set_theme(this.value);">
            <option value="">default</option>
            <option value="primary">primary</option>
            <option value="warning">warning</option>
            <option value="danger">danger</option>
            <option value="success">success</option>
            <option value="info">info</option>
            <option value="greensea" selected="selected">greensea</option>
            <option value="nephrite">nephrite</option>
            <option value="belizehole">belizehole</option>
            <option value="wisteria">wisteria</option>
            <option value="asphalt">asphalt</option>
            <option value="orange">orange</option>
            <option value="pumpkin">pumpkin</option>
            <option value="pomegranate">pomegranate</option>
            <option value="clouds">clouds</option>
            <option value="asbestos">asbestos</option>
          </select>
        </li>
      </ol>
      <div>6. Adjusting</div>
      <ol type="A">
        <li><button onclick="change_container();">resize container</button>
          <button onclick="resize_jsmind();">adusting</button></li>
        <li>expand/collapse</li>
        <ol>
          <li><button class="sub" onclick="expand();">expand node</button></li>
          <li><button class="sub" onclick="collapse();">collapse node</button></li>
          <li><button class="sub" onclick="toggle();">toggle node</button></li>
          <li><button class="sub" onclick="expand_to_level2();">expand to level 2</button></li>
          <li><button class="sub" onclick="expand_to_level3();">expand to level 3</button></li>
          <li><button class="sub" onclick="expand_all();">expand all</button></li>
          <li><button class="sub" onclick="collapse_all();">collapse all</button></li>
        </ol>
        <li>zoom</li>

        <button id="zoom-in-button" style="width:50px" onclick="zoomIn();">
          In
        </button>
        <button id="zoom-out-button" style="width:50px" onclick="zoomOut();">
          Out
        </button>
      </ol>

      <div>7. Multi Format</div>
      <ol type="A">
        <li>node_tree(default)</li>
        <ol>
          <li><button class="sub" onclick="show_data();">show data</button></li>
          <li><button class="sub" onclick="save_file();">save file</button></li>
          <li><input id="file_input" class="file_input" type="file"/></li>
          <li><button class="sub" onclick="open_file();">open file</button></li>
        </ol>
        <li>node_array</li>
        <ol>
          <li><button class="sub" onclick="get_nodearray_data();">show data</button></li>
          <li><button class="sub" onclick="save_nodearray_file();">save file</button></li>
          <li><input id="file_input_nodearray" class="file_input" type="file"/></li>
          <li><button class="sub" onclick="open_nodearray();">open file</button></li>
        </ol>
        <li>freemind(.mm)</li>
        <ol>
          <li><button class="sub" onclick="get_freemind_data();">show data</button></li>
          <li><button class="sub" onclick="save_freemind_file();">save file</button></li>
          <li><input id="file_input_freemind" class="file_input" type="file"/></li>
          <li><button class="sub" onclick="open_freemind();">open file</button></li>
        </ol>
      </ol>
    </div>
    <div id="jsmind_container"></div>
    <div style="display:none">
      <input class="file" type="file" id="image-chooser" accept="image/*"/>
    </div>

  </div>
</template>
<script>
//  import 'jsmind/style/jsmind.css'
// import jsMind from 'jsmind'
import jsMind from '@/assets/jsmind/js/jsmind.js'
import { axios } from '@/utils/request'
// window.jsMind = jsMind
// require('jsmind/js/jsmind.draggable.js')
require('@/assets/jsmind/js/jsmind.draggable.js')
export default {
  name: 'App',
  data: function () {
    return {
      active: true,
      jm: null
    }
  },
  mounted: function () {
    this.open_empty()
  },
  methods: {
    promptInfo (msg) {
        alert(msg)
    },
    load_jsmind: function () {

    },
    open_empty () {
        var options = {
            container: 'jsmind_container',
            theme: 'greensea',
            editable: true
        }
        this.jm = jsMind.show(options)
        // _jm = jsMind.show(options,mind);
    },
    open_json () {
        var mind = {
            'meta': {
                'name': 'jsMind remote',
                'author': 'hizzgdev@163.com',
                'version': '0.2'
            },
            'format': 'node_tree',
            'data': { 'id': 'root',
            'topic': 'jsMind',
            'children': [
                            { 'id': 'easy',
            'topic': 'Easy',
            'direction': 'left',
            'children': [
                    { 'id': 'easy1', 'topic': 'Easy to show' },
                    { 'id': 'easy2', 'topic': 'Easy to edit' },
                    { 'id': 'easy3', 'topic': 'Easy to store' },
                    { 'id': 'easy4', 'topic': 'Easy to embed' },
                    { 'id': 'other3', 'background-image': 'ant.png', 'width': '100', 'height': '100' }
                ] },
                { 'id': 'open',
                  'topic': 'Open Source',
                  'direction': 'right',
                  'children': [
                    { 'id': 'open1', 'topic': 'on GitHub', 'background-color': '#eee', 'foreground-color': 'blue' },
                    { 'id': 'open2', 'topic': 'BSD License' }
                ] },
                { 'id': 'powerful',
                  'topic': 'Powerful',
                  'direction': 'right',
                  'children': [
                    { 'id': 'powerful1', 'topic': 'Base on Javascript' },
                    { 'id': 'powerful2', 'topic': 'Base on HTML5' },
                    { 'id': 'powerful3', 'topic': 'Depends on you' }
                ] },
                { 'id': 'other',
                  'topic': 'test node',
                  'direction': 'left',
                  'children': [
                    { 'id': 'other1', 'topic': "I'm from local variable" },
                    { 'id': 'other2', 'topic': 'I can do everything' }
                ] }
            ] } }
        this.jm.show(mind)
    },
    open_ajax () {
      var mindUrl = '/public/jsondata_example.json'
      var mind = axios({
        url: mindUrl,
        method: 'get'
      })
      this.jm.show(mind)
      // this.jm.util.ajax.get(mindUrl, function (mind) {
      // })
    },
    screen_shot () {
        this.jm.screenshot.shootDownload()
    },
    show_data () {
        var mindData = this.jm.get_data()
        var mindString = jsMind.util.json.json2string(mindData)
        this.promptInfo(mindString)
    }
  }

}
</script>

<style scoped lang="less">
        li{margin-top:2px; margin-bottom:2px;}
        button{width:140px;}
        select{width:140px;}
        #layout{width:1230px;}
        #jsmind_nav{width:210px;height:600px;border:solid 1px #ccc;overflow:auto;float:left;}
        .file_input{width:100px;}
        button.sub{width:100px;}

        #jsmind_container{
            float:left;
            width:1000px;
            height:600px;
            border:solid 1px #ccc;
            background:#f4f4f4;
        }
</style>

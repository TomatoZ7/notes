<template>
  <zk-layout :showNavHeader="true">
    <div class="page-wrap">
      <!-- 分类 start -->
      <zk-toolbar>
        <div>
          <a-radio-group v-for="(item, index) in categoryList" :key="item.id" v-model="currentCateId" @change="handleCateChange(index)">
            <a-radio-button :value="item.id">
              {{ item.name }}
            </a-radio-button>
          </a-radio-group>
          <a-tabs default-active-key="1" v-for="(item, index) in childCate" :key="item.id">
            <a-tab-pane :key="item.id" :tab="item.name"></a-tab-pane>
          </a-tabs>
        </div>
      </zk-toolbar>
      <!-- 分类 end -->

      <!-- 列表 start -->
      <div class="box production-box">
        <div class="production-row clearfix">
          <template v-for="(item, index) in templateList">
            <production-col :key="item.id" @click.native="previewProduction(index)">
              <template v-slot>
                <video-template-card :dataSource="item" @editVideo="handleEditVideo(item.design_id)" />
              </template>
            </production-col>
          </template>
        </div>

        <a-empty v-if="!templateList.length" />
      </div>
      <!-- 列表 end -->
    </div>

    <!-- 预览 -->
    <preview-video-modal ref="preview-video-modal" :value.sync="displayPreviewModal" @cancel="closePreviewVideoModal" />
  </zk-layout>
</template>

<script>
import ProductionCol from '@/views/videoCreation/components/production-col.vue'
import PreviewVideoModal from '@/views/operation/videoDesign/comp/modals/preview-video-modal'
import ReachBottom from '@/views/videoCreation/mixins/reach-bottom.js'
import VideoTemplateCard from '@/views/videoCreation/components/video-template-card'
import moment from 'moment'

export default {
  components: {
    VideoTemplateCard,
    PreviewVideoModal,
    ProductionCol
  },
  data() {
    return {
      isLoading: false,
      requestParams: {
        stat: 1,
        categoryId: 1,
        page: 1,
        page_size: 20
      },
      templateList: [],
      displayPreviewModal: false, // 预览弹窗显隐
      categoryList: [], // 分类
      replaceFields: {
        title: 'name',
        value: 'id'
      },
      currentCateId: 0,
      childCate: [],
      defaultAll: {
        id: 0,
        name: '全部',
        pid: 0
      }
    }
  },
  mounted() {
    this.getTemplateList(true)
    this.getCategory()

    this.reachBottom = new ReachBottom('', {
      callback: this.loadmoreList,
      distance: 100,
      subscriber: window
    })
  },
  methods: {
    moment,

    /**
     * @description 分类数据
     */
    getCategory() {
      this.$http.get('/design_video_common/draft_template_category/list', { params: { stat: 1 } }).then(res => {
        if (res.stat == 1) {
          let categoryData = res.data.data
          categoryData.unshift(this.defaultAll)
          this.categoryList = categoryData
        }
      })
    },

    /**
     * @description: 获取模板列表
     * @param {boolean} refresh 是否刷新
     * @return {void}
     */
    async getTemplateList(refresh = false) {
      try {
        if (this.isLoading) {
          return
        }

        this.isLoading = true
        const res = await this.$http.get('/design_video_common/draft_template/list', {
          params: this.requestParams
        })

        if (refresh) {
          this.templateList = []
        }

        this.templateList = this.templateList.concat(res.data.data)
      } catch (error) {
        console.error(error)
      } finally {
        this.isLoading = false
      }
    },

    /**
     * @description: 获取更多列表
     * @return {void}
     */
    loadmoreList() {
      return new Promise(async resolve => {
        try {
          this.requestParams.page += 1
          await this.getTemplateList()
          resolve()
        } catch (error) {
          console.error(error)

          if (error && error.msg) {
            this.$message.error(error.msg)
          }
        }
      })
    },

    /**
     * @description 点击顶部导航回调
     */
    handleCateChange(index) {
      let childCate = this.categoryList[index].children || []
      // childCate.unshift(this.defaultAll)
      this.childCate = childCate
    },

    /**
     * @description 搜索
     */
    handleSearch() {
      this.requestParams.page = 1
      this.getTemplateList(true)
    },

    /**
     * @description: 视频点击
     * @param {number} index 列表索引
     * @return {void}
     */
    previewProduction(index) {
      const designInfo = this.templateList[index]
      const preview_config = designInfo.preview_config
      this.openPreviewVideoModal(JSON.parse(preview_config), {
        designId: designInfo.design_id,
        title: designInfo.name,
        url: designInfo.designs.video_url,
        width: designInfo.width,
        height: designInfo.width
      })
    },

    /**
     * @description: 打开预览弹窗
     * @param {object} previewConfig 预览视频配置
     * @param {object} previewVideoInfo 预览视频信息
     * @return {void}
     */
    openPreviewVideoModal(previewConfig, previewVideoInfo = {}) {
      this.displayPreviewModal = true
      this.$refs['preview-video-modal'].loadVideo(previewConfig, previewVideoInfo)
      this.$refs['preview-video-modal'].displayType = 'video'
    },

    /**
     * @description: 关闭预览弹窗
     * @return {void}
     */
    closePreviewVideoModal() {
      this.displayPreviewModal = false
      this.$refs['preview-video-modal'].display = false
    },

    /**
     * @description 编辑视频
     */
    handleEditVideo(designId) {
      this.$http.post('/operation/design_video/create_draft', { id: designId }).then(res => {
        if (res.stat == 1) {
          this.$router.push({
            name: 'shortVideoCreation',
            query: {
              draft_id: res.data.id
            }
          })
        } else {
          this.$message.error('操作失败:' + res.msg)
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
.page-wrap {
  position: relative;
  margin-bottom: 24px;
}

.box {
  display: inline-block;
  padding: 16px 24px 24px;
  background: #ffffff;
  border-radius: 2px;
  width: 100%;

  &.production-box {
    display: block;
    margin-top: 24px;
    padding: 16px 16px 16px;

    /deep/ .ant-input-search-icon {
      color: rgba(0, 0, 0, 0.25);
    }

    /deep/ .ant-tabs-bar {
      border-bottom: none;
    }
  }
}

.production-row {
  position: relative;
  width: 100%;
  box-sizing: border-box;
}

.ant-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 462px;
}
</style>
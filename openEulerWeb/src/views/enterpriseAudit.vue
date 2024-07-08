<template>
  <div class="enterpriseAudit">
    <PictureMain
        :enName="enName"
        :cnNameTitle="cnNameTitle"
        :processName="processName"
    ></PictureMain>
    <div class="contentBottom">
      <div class="content">
        <TitleInput
            :title="title"
            :placeholder="placeholder"
            @inputChange="inputChange"
        ></TitleInput>
        <ScreenCondition
            :textList="textList"
            :textTitle="textTitle"
            @handleChange="handleChange"
        ></ScreenCondition>
        <el-table :data="tableData" style="width: 100%; margin-top: 24px">
          <el-table-column
              prop="companyName"
              label="企业名称"
              width="240"
              show-overflow-tooltip
          >
            <template slot-scope="scoped">
            <span
                class="cursor"
                style="color: #002fa7"
                @click="goDetail(scoped.row.userUuid)"
            >{{ scoped.row.companyName }}</span>
            </template>
          </el-table-column>
          <el-table-column
              prop="telephone"
              label="手机号码"
              show-overflow-tooltip
          ></el-table-column>
          <el-table-column
              prop="companyMail"
              label="企业邮箱"
              width="240"
              show-overflow-tooltip></el-table-column>
          <el-table-column
              prop="username"
              label="用户名称"
              show-overflow-tooltip
          ></el-table-column>
          <el-table-column
              prop="logoName"
              label="企业LOGO"
              show-overflow-tooltip
          >
            <template slot-scope="scoped">
          <span
              class="cursor"
              style="color: #002fa7"
              @click="getDownload(scoped.row.logo)"
          >{{ scoped.row.logoName }}</span>
            </template>
          </el-table-column>
          <el-table-column
              prop="licenseName"
              label="营业执照"
              show-overflow-tooltip
          >
            <template slot-scope="scoped">
          <span
              class="cursor"
              style="color: #002fa7"
              @click="getDownload(scoped.row.license)"
          >{{ scoped.row.licenseName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="审核状态">
            <template slot-scope="scoped">
              <div v-if="scoped.row.status == 0" class="reviewStatus">
                审核中
              </div>
              <div v-if="scoped.row.status == 1" class="reviewStatus success">
                已通过
              </div>
              <div v-if="scoped.row.status == 2" class="reviewStatus noAllow">
                不通过
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template slot-scope="scoped">
              <div class="options" v-if="scoped.row.status == 0">
              <span
                  style="color: #002fa7"
                  @click="goDetail(scoped.row.userUuid)"
                  class="cursor"
              >审核</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :current-page="currentPage"
              :page-sizes="[10, 20, 50, 100]"
              :page-size="pageSize"
              layout="total,sizes, prev, pager, next, jumper"
              :total="total"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import PictureMain from "@/components/pictureMain.vue"
import TitleInput from "@/components/titleInput.vue"
import ScreenCondition from "@/components/screenCondition.vue"

export default {
  name: "enterpriseAudit",
  comments: {
    PictureMain,
    TitleInput,
    ScreenCondition,
  },
  data() {
    return {
      enName: "CERTIFICATION SERVICE",
      cnNameTitle: "openEuler兼容性测评服务",
      processName: "",
      title: "企业审核",
      textTitle: "审核状态",
      textList: [
        {name: "审核中", active: false},
        {name: "已通过", active: false},
        {name: "不通过", active: false},
      ],
      currentPage: 1,
      pageSize: 10,
      total: 100,
      tableData: [],
      placeholder: "请输入企业名称搜索内容",
      reviewStatus: [],
      inputValue: "",
    };
  },
  mounted() {
    this.getUsename();
    this.getInforMation();
  },
  methods: {
    getUsename() {
      this.axios
          .get("/user/getUserInfo", {})
          .then((res) => {
            if (res.data.code == 401) {
              this.getLogin();
            } else {
              let useName = res.data.result;
              this.$store.commit("changeStatus", useName);
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message);
          });
    },
    handleChange(value) {
      this.inputValue = value;
      this.getInforMation();
    },
    inputChange(value) {
      this.reviewStatus = value;
      this.getInforMation();
    },
    getInforMation() {
      let params = {
        companyName: this.inputValue,
        curPage: this.currentPage,
        pageSize: this.pageSize,
        status: this.reviewStatus,
      };
      this.axios
          .post("/companies/findByNameAndStatus", params)
          .then((response) => {
            if (response.data.code == 200) {
              this.tableData = response.data.result.records;
              this.total = response.data.result.total;
            } else {
              this.$message.error(response.data.result.errorMsg);
            }
          })
    },
    goDetail(uuid) {
      this.$router.push({
        path: "/enterpriseAuditDetails",
        query: {
          id: uuid,
        },
      });
    },
    async downHandler(url) {
      const res = await this.axios.get(url, {
        responseType: "blob",
      });
      const blob = new Blob([res.data], {
        type: "application/octet-stream",
      });
      let imgBase64 = URL.createObjectURL(blob);
      const disposition = decodeURI(
          res.headers["content-disposition"]
              .replaceAll("+", "%20")
              .replaceAll("%2B", "+")
      );
      //获取filename
      const fileName = disposition.split("filename=")[1];
      const a = document.createElement("a");
      a.href = imgBase64;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      a.remove();
    },
    getDownload(id) {
      this.$confirm(
          "文件由外部用户上传，下载后建议安全扫描后谨慎打开",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
            beforeClose: (action, instance, done) => {
              if (action === "confirm") {
                this.downHandler(`/companies/download?fileId=${id}`)
              }
              done();
            },
          }
      );
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getInforMation();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getInforMation();
    },
  },
};
</script>

<style lang="less" scoped>
.enterpriseAudit {
  .contentBottom {
    display: flex;
    justify-content: center;

    .content {
      width: 1416px;

      .reviewStatus {
        width: 72px;
        height: 24px;
        background: #52a3ff;
        border-radius: 2px;
        color: #ffffff;
        font-size: 14px;
        text-align: center;
        line-height: 24px;
      }

      .success {
        background: #24ab36;
      }

      .noAllow {
        background: #e32020;
      }

      .pagination {
        height: 100px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}
</style>
<style lang="less">
.enterpriseAudit {
  .el-table th.el-table__cell {
    background: #e5e8f0;
    color: #000000;
  }

  .el-table th.el-table__cell > .cell {
    padding-left: 40px;
  }

  .el-table .cell {
    padding-left: 40px;
  }
}
</style>
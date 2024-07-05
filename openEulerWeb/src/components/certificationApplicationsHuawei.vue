<template>
  <div class="certificationApplicationHuawei">
    <div
      class="topMenu"
      v-if="
        $store.state.useName.roleNames &&
        $store.state.useName.roleNames.includes('欧拉社区旗舰店')
      "
    >
      <div
        @click="isactive = 'true'"
        :class="{ active: isactive == 'true' }"
        style="margin-left: 0px"
      >
        测评审核
      </div>
      <div @click="isactive = 'false'" :class="{ active: isactive == 'false' }">
        兼容性数据审核
      </div>
    </div>
    <div class="content" v-show="isactive == 'true'">
      <TitleInput
        :title="title"
        placeholder="placeholder"
        @inputChange="inputChange"
      ></TitleInput>
      <ScreenCondition
        :textList="textList1"
        :textTitle="textTitle"
        @handleChange="handleChange1"
      ></ScreenCondition>
      <ScreenCondition
        :textList="textList2"
        :textTitle="textTitle1"
        @handleChange="handleChange2"
      ></ScreenCondition>
      <ScreenCondition
        :textList="textList3"
        :textTitle="textTitle2"
        @handleChange="handleChange3"
      ></ScreenCondition>
      <el-table :data="tableData" style="width: 100%; margin-top: 24px">
        <el-table-column
          prop="productName"
          label="测评名称"
          show-overflow-tooltip
          width="160"
        >
          <template slot-scope="scoped">
            <span
              style="color: #002fa7"
              @click="goDetails(scoped.row)"
              class="cursor"
              >{{ scoped.row.productName }}</span
            >
          </template>
        </el-table-column>
        <el-table-column
          prop="companyName"
          label="企业名称"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="productType"
          label="测评类型"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="productVersion"
          label="测评版本"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="osName"
          label="OS名称"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="osVersion"
          label="OS版本"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="hashratePlatformaNameList"
          label="算力平台"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="testOrganization"
          label="测试机构"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="applicationTime"
          label="申请日期"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="reviewer"
          label="审核人"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          prop="status"
          label="测评状态"
          show-overflow-tooltip
          width="160px"
        >
          <template slot-scope="scoped">
            <div
              class="processStatusFail"
              v-if="
                scoped.row.status && scoped.row.status.slice(-3) == '已驳回'
              "
            >
              {{ scoped.row.status }}
            </div>
            <div
              class="processStatus"
              v-if="
                scoped.row.status && scoped.row.status.slice(-3) != '已完成'
              "
            >
              {{ scoped.row.status }}
            </div>
            <div class="processStatusSuccess" v-else>
              {{ scoped.row.status }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scoped">
            <span class="edits cursor" @click="goDetails(scoped.row)">{{
              scoped.row.operation
            }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-agination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="sizes,prev,pager,next,jumper"
          :total="total"
        ></el-agination>
      </div>
    </div>
    <CompatibleHuawei v-show="isactive == false"></CompatibleHuawei>
  </div>
</template>
<script>
import TitleInput from "@/components/titleInput.vue";
import ScreenCondition from "@/components/screenCondition.vue";
import CompatibleView from "@/components/CompatibleView.vue";
import {
  authenticationStatus,
  testOrganization,
  industryType,
} from "@/assets/js/publicData";
export default {
  components: {
    CompatibleView,
    TitleInput,
    ScreenCondition,
  },
  data() {
    return {
      isactive: "true",
      title: "测评审核",
      textTitle: "测评状态",
      textTitle1: "测试机构",
      textTitle2: "产品类型",
      authenticationStatus: [],
      testOrganization: [],
      industryType: [],
      textList1: [],
      textList2: [],
      textList3: [],
      placeholder: "请输入产品名称搜索内容",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      inputValue: "",
      reviewStatus: [],
      productName: "",
      testOrgan: [],
      tableData: [],
    };
  },
  created() {
    this.initializingProcessFn();
    this.getTableList();
    if (this.$route.query.isactive) {
      this.isactive = this.$route.query.isactive;
    }
  },
  methods: {
    initializingProcessFn() {
      this.authenticationStatus = JSON.parse(
        JSON.stringify(authenticationStatus)
      );
      this.testOrganization = JSON.parse(JSON.stringify(testOrganization));
      this.industryType = JSON.parse(JSON.stringify(industryType));
      for (var i = 0; i < this.authenticationStatus.length; i++) {
        this.authenticationStatus[i] = {
          name: this.authenticationStatus[i],
          active: false,
        };
      }
      this.textList1 = JSON.parse(JSON.stringify(this.authenticationStatus));
      for (var i = 0; i < this.testOrganization.length; i++) {
        this.testOrganization[i] = {
          name: this.testOrganization[i],
          active: false,
        };
      }
      this.textList2 = JSON.parse(JSON.stringify(this.testOrganization));
      for (var i = 0; i < this.industryType.length; i++) {
        this.industryType[i] = {
          name: this.industryType[i],
          active: false,
        };
      }
      this.textList3 = JSON.parse(JSON.stringify(this.industryType));
    },
    inputChange(value) {
      this.currentPage = 1;
      this.inputValue = value;
      this.getTableList();
    },
    handleChange1(value) {
      this.currentPage = 1;
      this.testOrgan = value;
      this.getTableList();
    },
    handleChange2(value) {
      this.currentPage = 1;
      this.reviewStatus = value;
      this.getTableList();
    },
    handleChange3(value) {
      this.currentPage = 1;
      this.productType = value;
      this.getTableList();
    },
    getTableList() {
      let params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        productName: this.inputValue,
        productType: this.productType,
        status: this.testOrgan,
        testOrganization: this.reviewStatus,
      };
      this.axios.post("/software/reviewSoftwareList", params).then((response) => {
        if (response.data.code === 200) {
          this.tableData = response.data.result.list;
          this.total = reponse.data.result.total;
        } else if (response.data.code != 401) {
          this.$message.error(response.data.result.message);
        } else {
          return;
        }
      });
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.getTableList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableList();
    },
    goDetails(row) {
      this.$router.push({
        path: "/certificationDetailsHuawei",
        query: {
          softwareId: row.id,
        },
      });
    },
  },
};
</script>
<style lang="less" scoped>
.topMenu {
  width: 100%;

  height: 48px;
  background: #fdfefe;
  box-shadow: 0px 1px 4px 0px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  div {
    height: 46px;
    line-height: 46px;
    text-align: center;
    font-size: 16px;
    margin-left: 40px;
    color: #555;
    cursor: pointer;
  }
  .active {
    color: #002fa7;
    border-bottom: 2px solid #002fa7;
  }
}
.certificationApplication {
  display: flex;
  flex-direction: column;
  align-items: center;
  .content {
    width: 146px;
    .myCertified {
      height: 136px;
      text-align: center;
      line-height: 136px;
      span {
        font-size: 36px;
      }
    }
    .processStatus {
      height: 24px;
      background: #52a3ff;
      border-radius: 2px;
      color: #fff;
      text-align: center;
    }
    .processStatusSuccess {
      height: 24px;
      background: #24ab36;
      border-radius: 2px;
      color: #fff;
      text-align: center;
    }
    .processStatusFail {
      height: 24px;
      background: #e32020;
      border-radius: 2px;
      color: #fff;
      text-align: center;
    }
    .edits {
      color: #002fa7;
    }
    .pagination {
      height: 100px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}
</style>
<style lang="less">
.certificationApplication {
  .el-table th.el-table__cell {
    background: #e5e8f0;
    color: #000;
  }
  .el-table th.el-table__cell > .cell {
    padding-left: 40px;
  }
  .el-table .cell {
    padding-left: 40px;
  }
}
</style>

<template>
  <div class="mypage-sidebar">
    <div class="sidebar-title">Admin <span class="divider">/</span> <span class="mypage">My Page</span></div>
    <nav class="sidebar-nav">
      <div class="sidebar-section">
        내 정보
        <ul>
          <li v-for="(menu, index) in menuItems"
              :key="index"
              :class="{ active: currentMenu === menu.id }"
              @click="selectMenu(menu.id)">
            {{ menu.name }}
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>

<script>
export default {
  name: 'MyPageSidebar',
  props: {
    currentMenu: {
      type: String,
      required: true
    }
  },
  setup(props, { emit }) {
    const menuItems = [
      { id: 'profile.basic', name: '기본 정보' },
      { id: 'profile.password', name: '비밀번호 변경' }
    ];

    const selectMenu = (menuId) => {
      emit('update:currentMenu', menuId);
    };

    return {
      menuItems,
      selectMenu
    };
  }
};
</script>

<style>
.mypage-sidebar {
  width: 240px;
  background: white;
  padding: 40px 0;
  border-right: 1px solid #eee;
  height: 100%;
}

.sidebar-title {
  padding: 0 32px;
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 40px;
}

.divider {
  color: #999;
}

.mypage {
  color: #d6bcf7;
}

.sidebar-nav {
  padding: 0 16px;
}

.sidebar-section {
  margin-bottom: 32px;
  color: #666;
  font-size: 14px;
}

.sidebar-section ul {
  list-style: none;
  padding: 0;
  margin: 12px 0 0 0;
}

.sidebar-section li {
  padding: 12px 16px;
  cursor: pointer;
  border-radius: 4px;
  color: #333;
  font-size: 15px;
  transition: all 0.2s;
}

.sidebar-section li:hover {
  background: rgba(214, 188, 247, 0.1);
  color: #d6bcf7;
}

.sidebar-section li.active {
  background: rgba(214, 188, 247, 0.1);
  color: #d6bcf7;
  font-weight: 500;
}
</style> 
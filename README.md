# VoidMarket

VoidMarket คือปลั๊กอินตลาดและร้านค้าสำหรับ Minecraft Java Edition 26.1.2 / Paper และ Folia

ตอนนี้ปลั๊กอินมี 2 ระบบแยกกันชัดเจน:

1. **Player Shop / ร้านค้าผู้เล่น**
   ผู้เล่นสร้างร้านเอง ตั้งราคาเอง เติม stock เอง และซื้อขายกันเองผ่าน Virtual Shop

2. **Server Market / ตลาดกลางเซิร์ฟเวอร์**
   ตลาดกลางที่ระบบเซิร์ฟเวอร์ควบคุมสินค้าและราคา ผู้เล่นซื้อ/ขายกับระบบกลางได้ แต่ผู้เล่นเพิ่มสินค้าเองหรือตั้งราคาเองไม่ได้

## สิ่งที่ต้องติดตั้ง

- Paper หรือ Folia 26.1.2
- Java ตาม `pom.xml`
- Vault
- Economy plugin เช่น EssentialsX Economy
- LuckPerms แนะนำสำหรับ permission
- PlaceholderAPI ไม่บังคับ

## วิธี Build

ในโฟลเดอร์ `VoidMarket`:

```bash
mvn clean package
```

ไฟล์ปลั๊กอินจะอยู่ที่:

```text
target/voidmarket-1.0.0.jar
```

## วิธีติดตั้ง

1. ใส่ `voidmarket-1.0.0.jar` ในโฟลเดอร์ `plugins/`
2. ติดตั้ง Vault และ EssentialsX Economy
3. Start server หนึ่งครั้งให้ปลั๊กอินสร้าง config
4. ตั้งค่า `plugins/VoidMarket/config.yml`
5. Restart server หรือใช้ `/pshop reload` และ `/market reload`

## Player Shop คืออะไร

Player Shop คือร้านค้าผู้เล่นแบบ Virtual Shop:

- ไม่ใช้ Chest Shop
- ไม่ผูกกับ block หรือ chest
- เจ้าของร้าน offline ก็ยังขายของได้
- stock เก็บใน database
- เงินเข้าผู้ขายผ่าน Vault Economy
- มีภาษีจาก `tax-percent`
- มีปุ่ม copy shopId จาก `/pshop list`

### วิธีตั้งร้านค้าผู้เล่น

1. ถือไอเทมที่ต้องการขาย
2. ใช้คำสั่ง `/pshop create <ราคา>`
3. ยืนยันใน GUI
4. เติมหรือถอน stock ได้ที่ `/pshop manage`
5. ผู้เล่นอื่นซื้อได้จาก `/pshop browse`

ตัวอย่าง:

```text
/pshop create 250
```

## คำสั่ง Player Shop

```text
/pshop
/pshop help
/pshop create <ราคา>
/pshop browse
/pshop list
/pshop manage
/pshop stock <shopId>
/pshop setprice <shopId> <ราคา>
/pshop remove <shopId>
/pshop reload
/pshop storage
```

### `/pshop`

เปิดเมนูร้านค้าผู้เล่นของตัวเอง

Permission:

```text
voidmarket.use
```

### `/pshop help`

ดูวิธีใช้งานร้านค้าผู้เล่น

### `/pshop create <ราคา>`

สร้างร้านจากไอเทมในมือ เช่น:

```text
/pshop create 500
```

Permission:

```text
voidmarket.shop.create
```

### `/pshop browse`

เปิด GUI ร้านค้าผู้เล่นทั้งหมด ซื้อขายกับผู้เล่นคนอื่น

### `/pshop list`

แสดงร้านของตัวเองใน chat พร้อมปุ่ม:

```text
[COPY ID] [STOCK] [PRICE] [REMOVE]
```

ใช้ปุ่ม `[COPY ID]` เพื่อคัดลอก shopId แบบเต็มได้ทันที

### `/pshop manage`

เปิด GUI จัดการร้านของตัวเอง เช่น ดูร้าน แก้ราคา เติม stock และลบร้าน

### `/pshop stock <shopId>`

เปิด GUI เติมหรือถอน stock ของร้าน

### `/pshop setprice <shopId> <ราคา>`

เปลี่ยนราคาของร้าน

### `/pshop remove <shopId>`

ลบร้านและคืน stock ที่เหลือให้เจ้าของร้าน

Permission:

```text
voidmarket.shop.remove
```

## Server Market คืออะไร

Server Market คือตลาดกลางของเซิร์ฟเวอร์:

- ผู้เล่นซื้อไอเทมจากระบบกลางได้
- ผู้เล่นขายไอเทมให้ระบบกลางได้
- สินค้าอยู่ใน `config.yml`
- ราคา base/current/min/max อยู่ใน config และ data
- ราคาขึ้นเมื่อมีคนซื้อเยอะ
- ราคาขายลงเมื่อมีคนขายเยอะ
- ราคาค่อย ๆ กลับสู่ base price ตามเวลา
- ข้อมูลราคา dynamic เก็บใน `server-market-data.yml`

Server Market ไม่ใช่ Player Shop และไม่ได้มาจากผู้เล่น

## คำสั่ง Server Market

```text
/market
/voidmarket market
/market help
/market data
/market trend <item>
/market reload
/market reset <item>
/market setbuy <item> <price>
/market setsell <item> <price>
```

### `/market`

เปิด GUI ตลาดกลางเซิร์ฟเวอร์

Permission:

```text
voidmarket.market.use
```

### `/voidmarket market`

เปิด GUI ตลาดกลางผ่านคำสั่งหลักของปลั๊กอิน

### `/market data`

ดูจำนวนสินค้า จำนวนหมวด และสถานะ dynamic pricing

Permission:

```text
voidmarket.market.admin
```

### `/market trend <item>`

ดูแนวโน้มราคาของสินค้า เช่น:

```text
/market trend STONE
```

### `/market reload`

รีโหลด config/messages และรายการ Server Market โดยไม่ลบข้อมูลราคา dynamic

Permission:

```text
voidmarket.market.reload
```

### `/market reset <item>`

รีเซ็ตราคา item กลับไปที่ base price

Permission:

```text
voidmarket.market.reset
```

### `/market setbuy <item> <price>`

ตั้งราคา buy ของสินค้า

Permission:

```text
voidmarket.market.edit
```

### `/market setsell <item> <price>`

ตั้งราคา sell ของสินค้า

Permission:

```text
voidmarket.market.edit
```

## วิธีใช้ GUI Server Market

1. ใช้ `/market`
2. เลือกหมวดสินค้า
3. ในหน้ารายการสินค้า:
   - คลิกซ้าย = ซื้อ 1 ชุด
   - คลิกขวา = ขาย 1 ชุด
   - Shift click = เปิดหน้าปรับจำนวน
4. ในหน้าปรับจำนวนใช้ปุ่ม `-64`, `-32`, `-16`, `-1`, `+1`, `+16`, `+32`, `+64`
5. กดยืนยัน BUY หรือ SELL

สถานะราคา:

```text
UP     = ราคากำลังขึ้น
DOWN   = ราคากำลังลง
STABLE = ราคาปกติ
```

## หมวด Server Market

```text
blocks_shop
colored_blocks_shop
wood_shop
nature_shop
farming_shop
food_shop
materials_shop
mobdrops_common_shop
mobdrops_rare_shop
utility_shop
```

## Config สำคัญ

```yaml
language: en
tax-percent: 5.0
max-shops-default: 2
max-shops-vip: 5
max-shops-svip: 10
max-shops-staff: 50
max-shop-stock: 3456
allow-buy-own-shop: false
```

Server Market:

```yaml
server-market:
  enabled: true
  economy:
    dynamic-pricing: true
    price-change-percent-buy: 2.5
    price-change-percent-sell: 2.0
    price-recovery-percent: 1.0
    recovery-interval-minutes: 60
    min-multiplier: 0.5
    max-multiplier: 3.0
    default-sell-ratio: 0.25
```

## Storage

ค่าเริ่มต้นคือ SQLite:

```yaml
storage:
  type: sqlite
  sqlite:
    file: market.db
```

MySQL/MariaDB:

```yaml
storage:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: voidmarket
    username: root
    password: ''
    useSSL: false
```

Player Shop ใช้ database เดิม ส่วน Server Market เก็บราคา dynamic แยกใน:

```text
server-market-data.yml
```

## ภาษา

ค่าเริ่มต้น:

```yaml
language: en
```

ภาษาไทย:

```yaml
language: th
```

ไฟล์ข้อความ:

```text
messages_en.yml
messages_th.yml
```

## Permissions ทั้งหมด

Player Shop:

```text
voidmarket.use
voidmarket.shop.create
voidmarket.shop.remove
voidmarket.shop.limit.default
voidmarket.shop.limit.vip
voidmarket.shop.limit.svip
voidmarket.shop.limit.staff
voidmarket.admin
voidmarket.reload
voidmarket.bypass
```

Server Market:

```text
voidmarket.market.use
voidmarket.market.admin
voidmarket.market.reload
voidmarket.market.edit
voidmarket.market.reset
```

## ตัวอย่าง LuckPerms

```text
lp group default permission set voidmarket.use true
lp group default permission set voidmarket.shop.create true
lp group default permission set voidmarket.shop.limit.default true
lp group default permission set voidmarket.market.use true

lp group vip permission set voidmarket.shop.limit.vip true
lp group svip permission set voidmarket.shop.limit.svip true

lp group admin permission set voidmarket.admin true
lp group admin permission set voidmarket.reload true
lp group admin permission set voidmarket.bypass true
lp group admin permission set voidmarket.market.admin true
lp group admin permission set voidmarket.market.reload true
lp group admin permission set voidmarket.market.edit true
lp group admin permission set voidmarket.market.reset true
```

## PlaceholderAPI

```text
%voidmarket_shops%
%voidmarket_items_sold%
%voidmarket_shop_trend_<shopId>%
%voidmarket_shop_stock_<shopId>%
```

## สรุปสำหรับผู้เล่น

ใช้ร้านผู้เล่น:

```text
/pshop
/pshop create <ราคา>
/pshop browse
/pshop list
/pshop manage
```

ใช้ตลาดกลางเซิร์ฟเวอร์:

```text
/market
```

## สรุปสำหรับแอดมิน

```text
/pshop reload
/pshop storage
/market data
/market reload
/market reset <item>
/market setbuy <item> <price>
/market setsell <item> <price>
```

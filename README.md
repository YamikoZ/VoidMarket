# VoidMarket

VoidMarket คือปลั๊กอินเศรษฐกิจสำหรับเซิร์ฟเวอร์ Paper/Folia ของ VoidSMP มีระบบตลาดกลาง, ราคาแบบ Dynamic, ร้านค้าผู้เล่นแบบ Virtual Shop, GUI, ฐานข้อมูล SQLite/MySQL, Vault Economy และ PlaceholderAPI แบบ optional

ปลั๊กอินนี้ **ไม่ใช่ Chest Shop** และ **ไม่ผูกกับตำแหน่ง block/chest** ร้านค้าผู้เล่นทั้งหมดเป็นร้าน virtual ที่เก็บ stock ในฐานข้อมูล

## มีอะไรบ้าง

- ตลาดกลางของเซิร์ฟเวอร์ผ่าน `/market`
- GUI ซื้อ/ขายสินค้าในตลาดกลาง
- ระบบราคา Dynamic ตาม demand, supply และ stock
- แยกหมวดสินค้า เช่น Blocks, Food, Tools, Ores, Mob Drops, Special
- ร้านค้าผู้เล่นแบบ Virtual Shop ผ่าน `/pshop`
- ผู้เล่นสร้างร้านจากไอเทมในมือ
- ผู้เล่นเติม stock/จัดการราคา/ลบร้านผ่านคำสั่งและ GUI
- ผู้เล่นอื่นซื้อของจากร้านได้ แม้เจ้าของร้าน offline
- เงินเข้าผู้ขายผ่าน Vault Economy
- รองรับภาษาจาก `messages_en.yml` และ `messages_th.yml`
- รองรับสีแบบ `&a`, hex, และ MiniMessage gradient
- รองรับ SQLite เป็นค่าเริ่มต้น
- รองรับ MySQL/MariaDB สำหรับเซิร์ฟจริง
- มี transaction log ในฐานข้อมูล
- มี PlaceholderAPI ถ้าติดตั้งไว้

## ⚠️ การแจกจ่าย

**โปรเจกต์นี้ไม่แจกซอร์สโค้ด (closed source)**

- repo นี้มีเฉพาะ README และไฟล์ที่ build แล้ว (jar)
- ห้ามแก้ไข, decompile, reverse engineer, หรือ redistribute ในรูปแบบที่ดัดแปลง
- ใช้งานได้ฟรีบนเซิร์ฟเวอร์ของคุณ แต่ไม่อนุญาตให้นำซอร์ส (หากได้มาด้วยวิธีใด ๆ) ไปเผยแพร่ต่อ
- ถ้าพบบั๊กหรืออยากขอฟีเจอร์ เปิด Issue ใน repo นี้ได้

## สิ่งที่ต้องมี

- Paper หรือ Folia รุ่นที่ระบุใน Release
- Java 21+ (ตามเวอร์ชันที่ระบุใน Release)
- Vault
- Economy plugin เช่น EssentialsX Economy
- LuckPerms แนะนำสำหรับจัด permission
- PlaceholderAPI ไม่บังคับ

## Folia

VoidMarket ประกาศ `folia-supported: true` แล้ว และใช้ Paper/Folia scheduler API สำหรับงานที่ต้องกลับไปหา player/global thread

## วิธีดาวน์โหลด

ดาวน์โหลด jar ล่าสุดจากหน้า [Releases](https://github.com/YamikoZ/VoidMarket/releases)

ไม่ต้อง build เอง — โปรเจกต์นี้ไม่แจกซอร์ส

## วิธีติดตั้ง

1. ดาวน์โหลด `VoidMarket-x.x.x.jar` จาก [Releases](https://github.com/YamikoZ/VoidMarket/releases)
2. นำ jar ไปใส่ในโฟลเดอร์ `plugins/`
3. ติดตั้ง `Vault` และ economy plugin เช่น `EssentialsX`
4. Start server หนึ่งครั้งเพื่อให้ plugin สร้าง config
5. ตั้งค่า `config.yml` ตามต้องการ
6. Restart server หรือใช้ `/market reload`

## คำสั่งที่ผู้เล่นใช้บ่อย

ผู้เล่นทั่วไปจำหลัก ๆ แค่นี้ก็พอ:

```text
/market
/market prices
/market buy <item> <amount>
/market sell <item> <amount>
/pshop help
/pshop create <ราคา>
/pshop browse
/pshop manage
```

## คำสั่ง Market ทั้งหมด

### `/market`

เปิด GUI ตลาดกลางของเซิร์ฟเวอร์

ใช้สำหรับดูสินค้า, ราคา, stock, demand, supply และซื้อ/ขายผ่าน GUI

Permission:

```text
voidmarket.use
```

### `/market help`

แสดงคำสั่งทั้งหมดของระบบตลาด

Permission:

```text
voidmarket.use
```

### `/market buy <item> <amount>`

ซื้อสินค้าจากตลาดกลาง

ตัวอย่าง:

```text
/market buy diamond 1
/market buy stone 64
```

Permission:

```text
voidmarket.buy
```

### `/market sell <item> <amount>`

ขายสินค้าเข้าตลาดกลาง

ตัวอย่าง:

```text
/market sell diamond 1
/market sell stone 64
```

Permission:

```text
voidmarket.sell
```

### `/market price <item>`

ดูราคาซื้อ, ราคาขาย และ stock ของสินค้าชิ้นเดียว

ตัวอย่าง:

```text
/market price diamond
/market price iron_ingot
```

Permission:

```text
voidmarket.use
```

### `/market prices`

เปิด GUI รายการราคาสินค้าทั้งหมด

Permission:

```text
voidmarket.use
```

### `/market storage`

ดูว่า plugin ใช้ storage แบบไหน เช่น SQLite หรือ MySQL

Permission:

```text
voidmarket.use
```

### `/market admin`

เปิด GUI แอดมินของตลาด

Permission:

```text
voidmarket.admin
```

### `/market reload`

Reload config และ messages

Permission:

```text
voidmarket.reload
```

### `/market migrate`

ตรวจ/รัน schema migration ของฐานข้อมูล

Permission:

```text
voidmarket.admin
```

### `/market backup`

คำสั่งสำหรับระบบ backup/check backup ของฐานข้อมูล

Permission:

```text
voidmarket.admin
```

## คำสั่ง Player Shop ทั้งหมด

Player Shop ของ VoidMarket เป็นร้านค้าแบบ virtual ทั้งหมด ไม่มี chest และไม่ต้องวาง block

### `/pshop`

เปิดเมนูร้านค้าผู้เล่นของตัวเอง

Permission:

```text
voidmarket.use
```

### `/pshop help`

แสดงวิธีใช้งานร้านค้าผู้เล่น และวิธีสร้างร้าน

Permission:

```text
voidmarket.use
```

### `/pshop create <price>`

สร้างร้านค้าจากไอเทมที่ถืออยู่ในมือ

ตัวอย่าง:

```text
/pshop create 250
```

ขั้นตอน:

1. ถือไอเทมที่ต้องการขายไว้ในมือ
2. ใช้คำสั่ง `/pshop create <ราคา>`
3. กดยืนยันใน GUI
4. ระบบจะดึงไอเทมจากมือเข้า stock เริ่มต้นของร้าน
5. ผู้เล่นอื่นซื้อได้จาก `/pshop browse`

Permission:

```text
voidmarket.shop.create
```

### `/pshop browse`

เปิด GUI ตลาดร้านค้าผู้เล่นทั้งหมด

ใช้ซื้อของจากร้านผู้เล่นคนอื่น แม้เจ้าของร้าน offline

Permission:

```text
voidmarket.use
```

### `/pshop list`

ดูรายการร้านค้าของตัวเองในแชท พร้อมปุ่มคลิก:

- `[COPY ID]` คัดลอก `shopId` แบบเต็มทันที
- `[STOCK]` เติมคำสั่ง `/pshop stock <shopId>` ให้ในช่องแชท
- `[PRICE]` เติมคำสั่ง `/pshop setprice <shopId> <ราคา>` ให้ในช่องแชท
- `[REMOVE]` เติมคำสั่ง `/pshop remove <shopId>` ให้ในช่องแชท

ตัวอย่างการใช้งาน:

```text
/pshop list
```

ในแชทจะเห็นประมาณนี้:

```text
1. Diamond | Price 250 | Stock 64 | ID 4b3f1c9a [COPY ID] [STOCK] [PRICE] [REMOVE]
```

`ID 4b3f1c9a` เป็นรหัสย่อให้อ่านง่าย ส่วนปุ่ม `[COPY ID]` จะ copy UUID เต็มของร้าน

Permission:

```text
voidmarket.use
```

### `/pshop manage`

เปิด GUI จัดการร้านค้าของตัวเอง

ใช้ดูร้าน, จัดการ stock, เปลี่ยนราคา หรือเตรียมลบร้าน

Permission:

```text
voidmarket.use
```

### `/pshop stock <shopId>`

เปิด GUI เติม stock ของร้านที่เลือก

ตัวอย่าง:

```text
/pshop stock 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

Permission:

```text
voidmarket.use
```

### `/pshop setprice <shopId> <price>`

เปลี่ยนราคาขายของร้าน

ตัวอย่าง:

```text
/pshop setprice 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx 500
```

Permission:

```text
voidmarket.use
```

### `/pshop remove <shopId>`

ลบร้าน และคืน stock ที่เหลือกลับเข้า inventory ของเจ้าของร้าน

ตัวอย่าง:

```text
/pshop remove 4b3f1c9a-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

Permission:

```text
voidmarket.shop.remove
```

## วิธีตั้งร้านแบบสั้นสำหรับผู้เล่น

ส่งข้อความนี้ให้ผู้เล่นอ่านได้เลย:

```text
1. ถือไอเทมที่ต้องการขาย
2. พิมพ์ /pshop create <ราคา>
3. กดยืนยันใน GUI
4. ใช้ /pshop manage เพื่อจัดการร้าน
5. คนอื่นซื้อได้จาก /pshop browse
```

ตัวอย่าง:

```text
/pshop create 250
```

## ระบบราคา Dynamic

ราคาตลาดกลางไม่ได้คงที่ตลอด ระบบจะคำนวณจาก:

- มีคนซื้อเยอะ ราคาเพิ่ม
- มีคนขายเยอะ ราคาลด
- stock น้อย ราคาเพิ่ม
- stock เยอะ ราคาถูกลง
- ราคาถูกจำกัดด้วย min/max multiplier ใน config

สูตรแนวคิด:

```text
priceMultiplier =
  1.0
  + demandFactor
  - supplyFactor
  + scarcityFactor
```

## Storage / ฐานข้อมูล

ค่าเริ่มต้นใช้ SQLite:

```yaml
storage:
  type: sqlite
  sqlite:
    file: market.db
```

ถ้าต้องการใช้ MySQL หรือ MariaDB:

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

ตารางฐานข้อมูลจะถูกสร้างอัตโนมัติตอน plugin เปิด

## Language / ภาษา

ค่าเริ่มต้นเป็น English:

```yaml
language: en
```

ถ้าต้องการภาษาไทย:

```yaml
language: th
```

ไฟล์ข้อความ:

```text
messages_en.yml
messages_th.yml
```

ถ้าไฟล์ภาษาหาย plugin จะ fallback ไปใช้ English

## Permissions ทั้งหมด

```text
voidmarket.use
voidmarket.buy
voidmarket.sell
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

## ตัวอย่าง LuckPerms

```text
lp group default permission set voidmarket.use true
lp group default permission set voidmarket.buy true
lp group default permission set voidmarket.sell true
lp group default permission set voidmarket.shop.create true
lp group default permission set voidmarket.shop.limit.default true

lp group vip permission set voidmarket.shop.limit.vip true
lp group svip permission set voidmarket.shop.limit.svip true

lp group admin permission set voidmarket.admin true
lp group admin permission set voidmarket.reload true
lp group admin permission set voidmarket.bypass true
```

## PlaceholderAPI

ถ้าติดตั้ง PlaceholderAPI จะใช้ placeholder ได้:

```text
%voidmarket_price_<item>%
%voidmarket_stock_<item>%
%voidmarket_trend_<item>%
%voidmarket_shops%
%voidmarket_daily_volume_<item>%
```

ตัวอย่าง:

```text
%voidmarket_price_diamond%
%voidmarket_stock_diamond%
%voidmarket_trend_diamond%
```

## Config สำคัญ

```yaml
language: en
tax-percent: 5.0
daily-stock-limit: 5000
max-shops-default: 2
max-shops-vip: 5
max-shops-svip: 10
allow-buy-own-shop: false
```

ความหมาย:

- `tax-percent` ภาษีร้านค้าผู้เล่น
- `daily-stock-limit` จำกัดการขายเข้า market ต่อวัน
- `max-shops-default` จำนวนร้านของผู้เล่นทั่วไป
- `max-shops-vip` จำนวนร้านของ VIP
- `max-shops-svip` จำนวนร้านของ SVIP
- `allow-buy-own-shop` อนุญาตให้ซื้อของร้านตัวเองหรือไม่

## Item Blocklist

ตั้งไอเทมที่ห้ามซื้อขายได้ใน config:

```yaml
blocked-items:
  - BEDROCK
  - COMMAND_BLOCK
```

## หมวดหมู่สินค้า

ตัวอย่าง:

```yaml
categories:
  Blocks:
    - STONE
    - OAK_LOG
  Food:
    - BREAD
    - COOKED_BEEF
  Ores:
    - COAL
    - IRON_INGOT
    - DIAMOND
```

## สรุปสำหรับแอดมิน

แอดมินควรรู้คำสั่งเหล่านี้:

```text
/market admin
/market reload
/market storage
/market migrate
/market backup
```

และควรตั้ง permission ให้ผู้เล่นอย่างน้อย:

```text
voidmarket.use
voidmarket.buy
voidmarket.sell
voidmarket.shop.create
voidmarket.shop.limit.default
```

## สรุปสำหรับผู้เล่น

ผู้เล่นทั่วไปใช้จริงประมาณนี้:

```text
/market
/market prices
/market buy <item> <amount>
/market sell <item> <amount>
/pshop help
/pshop create <ราคา>
/pshop browse
/pshop manage
```

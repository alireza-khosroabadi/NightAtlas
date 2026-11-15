# Night Atlas

یک راهنمای جیبی انگلیسی برای سه صورت فلکی. از اطلس یک جرم را انتخاب کن، طرح آن را ببین و رصدت را ثبت کن. دفتر رصد روی دستگاه ذخیره می‌شود. کاتالوگ، نمودارها و راهنمای داخل برنامه آفلاین‌اند.

## اجرا

این پوشه را به‌عنوان پروژه در Android Studio باز کن و ماژول `app` را اجرا کن. پیش‌نیازها JDK 17 یا بالاتر، Android SDK 37 و دستگاه یا شبیه‌ساز API 24 یا بالاتر هستند. Gradle Wrapper داخل پروژه است.

```sh
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

APK در `app/build/outputs/apk/debug/app-debug.apk` ساخته می‌شود. مخزن‌های پیش‌فرض Google Maven و Maven Central هستند. در شبکه‌ای که به آینهٔ Google Maven نیاز دارد، آدرس آن را با `-PgoogleMavenMirror=...` بده.

## مسیرهای قابل امتحان

| از کجا شروع کنی | چه چیزی دیده می‌شود |
| --- | --- |
| Atlas → Lyra → Log observation | مقصد تایپ‌شده، انتقال مدل با `TypedDestination` به یک bottom sheet و جایگزینی برگهٔ تأیید با رسید از طریق `PopUpToAndNavigate` |
| Observation logged → View journal → Lyra | مسیریابی بین دو فیچر با `AppServiceId` و پارامتر، بدون وابستگی فیچرها به یکدیگر |
| Journal → Field guide | پردازش محلی `TAB_BAR`. نوار پایین از همان کنترلر ریشه استفاده می‌کند و وضعیت تب را ذخیره می‌کند |
| Atlas → Field kit → Routes | مسیر رشته‌ای ثبت‌شده، کارت JSON، حذف پارامتر null، انتقال عنوان، شناسهٔ مستقیم، alias دقیق و نرمال‌شده، alias وابسته به path، دیپ‌لینک معمولی و hub و مقصد جایگزین برای سرویس ناشناخته |
| Field kit → Android | WebView با محتوای محلی، مرورگر خارجی، شماره‌گیر، تنظیمات برنامه و زیست‌سنجی. Intent یک action سریال‌شده را به Activity می‌دهد و صف pending پس از آماده‌شدن گراف آن را مصرف می‌کند |
| Field kit → Back stack → Stop 1 → Stop 2 → Stop 3 | `Back` و `NavigateUp`، بازگشت به ریشه، pop به مقصد رشته‌ای یا تایپ‌شده، حذف inclusive، حذف تعدادی صفحه، جایگزینی صفحه و پاک‌سازی پشته |
| Field kit → Routes → Open the guide tab | ارسال `BOTTOM_BAR` همراه `tabScreen` از بیرون فیچر به تب مشخص دفتر |
| Field kit → Routes → Open tonight’s target by service | resolver یک URI برمی‌گرداند و router آن را دوباره dispatch می‌کند. نمونه‌های دیگر به همین ترتیب action، عملیات پشته و مقصد نوار پایین برمی‌گردانند |

برای آزمایش دیپ‌لینک از بیرون برنامه، روی دستگاهی که APK را نصب دارد اجرا کن. فرمان اول برنامه را از ابتدا اجرا می‌کند. فرمان دوم به Activity باز تحویل داده می‌شود.

```sh
adb shell am start -S -a android.intent.action.VIEW -d 'nightatlas://star/lyra/record' dev.nightatlas
adb shell am start -a android.intent.action.VIEW -d 'nightatlas://hub/field/guide' dev.nightatlas
adb shell am start -a android.intent.action.VIEW -d 'nightatlas://unmapped' dev.nightatlas
```

## مقایسه با پیاده‌سازی مرجع

مقایسه بر اساس سورس `core/navigation` و چهار فیچر در کامیت `7079132b5` انجام شده است. هر ردیف یک رفتار مسیریابی و راه امتحان آن در اطلس شب را نشان می‌دهد.

| مرجع | رفتار | مسیر اجرای نمونه |
| --- | --- | --- |
| `charge` | انتخاب صفحهٔ ورود یا تأیید با اولویت شمارهٔ مرحله نسبت به مسیر، انتقال مدل به برگهٔ پایین و رسید | پیوند `nightatlas://star/lyra/record?step=1` جزئیات شلیاق را باز می‌کند. با `step=2` برگهٔ ثبت باز می‌شود. در رسید، «Finish and return to the atlas» پشته را پاک می‌کند و گراف تب‌ها را از نو باز می‌کند. |
| `internetPackage` | ورود مستقیم به شروع، انتخاب یا مرور از طریق سرویس واحد و بازیابی آرگومان در ViewModel مقصد | «Atlas → Observing plan → Choose a target → Review plan». پیوند `nightatlas://plan?step=2` انتخاب را مستقیم باز می‌کند. `nightatlas://plan/review?id=lyra` به مرور شلیاق می‌رود. `ReviewPlanViewModel` آرگومان تایپ‌شده را از `SavedStateHandle` می‌خواند. |
| `contacts` | بازگرداندن نتیجه به ورودی قبلی پشته و تفکیک دو نمونهٔ یک مقصد با آرگومان متفاوت | در «Observing plan» یک هدف انتخاب کن، دوباره انتخاب‌گر را باز کن و با Back انصراف بده. هدف قبلی حفظ می‌شود. در «Field kit → Back stack → Stop 2»، «Visit this stop again» بازگشت به بازدید اول یا جایگزینی از همان بازدید را نشان می‌دهد. |
| `club` | تشخیص تب داخلی از اکشن سرویس، نوار پایین، پیوند و `TAB_BAR`، و ارسال مقصد غیرتب به روتر | در «Journal»، از «Shortcut to …» نوع اکشن را انتخاب کن. Back پس از چند تغییر تب به اطلس می‌رود. `nightatlas://book/field-guide` از بیرون وارد تب می‌شود. `nightatlas://journal/observation/orion` صفحهٔ جزئیات را باز می‌کند. |
| `core/navigation` و میزبان | تحویل یک‌بارهٔ درخواست‌های pending به‌ترتیب ورود | «Field kit → Android → Pending requests». اول «Queue the Orion action» و سپس «Queue the Lyra link» را بزن و از صف خارج شو. شلیاق باز می‌شود و Back به شکارچی می‌رسد. |
| میزبان دیپ‌لینک | پیوند بدون host، خواندن query و رجوع به URI ثبت‌شده در گراف برای پیوند قدیمی | `nightatlas:///plan/pick` مقصد انتخاب را باز می‌کند. در `nightatlas://hub/plan/pick?path=review&id=lyra` مقدار query بر مسیر اولویت دارد. `nightatlas://legacy/kit` از طریق تطبیق URI در NavGraph به میز ابزار می‌رود. |

### آزمایش ورودی دلخواه

از «Field kit → Routes → Try a route» می‌توان یک اکشن JSON، شناسهٔ سرویس با پارامتر، URI یا مسیر رشته‌ای فرستاد. نمونه‌های زیر را در تب «JSON action» وارد کن.

| ورودی | نتیجه |
| --- | --- |
| `{"destination":"plan","destinationType":"SERVICE","parameters":{"step":"3","id":"lyra"}}` | مرور برنامه برای شلیاق |
| `{"destination":"plan","destinationType":"SERVICE","parameters":{"step":"3"}}` | سرویس ثبت شده ولی شناسهٔ هدف لازم است و پیام خطا در همان صفحه دیده می‌شود |
| `{"destination":"next-season","destinationType":"SERVICE"}` | صفحهٔ مقصد پشتیبانی‌نشده با نام اصلی سرویس |
| `{"destination":"WEB_VIEW","destinationType":"SERVICE"}` | پیام مقصد در دسترس نیست به‌دلیل نبودن URL |
| `{"destination":"*123#","destinationType":"TEL_DEEP_LINK"}` | بازشدن شماره‌گیر با کد واردشده |
| `{"destination":"https://nightatlas.invalid/field-guide","destinationType":"old-type"}` | نوع ناشناختهٔ کارت قدیمی به WebView راهنمای آفلاین می‌رسد |

در تب «Service ID»، یک شناسهٔ ثبت‌نشده خطای مستقیم سرویس می‌دهد. در تب «Route name»، `trail/start` مسیر ثبت‌شده و `trail/end` مسیر تعریف‌شده فقط در گراف است. خطاهای ثبت تکراری و alias بدون مقصد با `RegistryContractTest` روی رجیستری جداگانه قابل تکرارند.

## ساختار

| ماژول | مسئولیت |
| --- | --- |
| `:domain` | مدل صورت فلکی و قراردادهای اطلس و دفتر رصد، مستقل از Android |
| `:data` | کاتالوگ داخلی و نگه‌داری رصدها در SharedPreferences |
| `:core:navigation` | container، router، registry، route handlerها، مدل action، دیپ‌لینک، pending و عملیات پشته |
| `:core:ui` | تم تیره، چیدمان صفحه‌ها و ردیف‌های قابل انتخاب |
| `:feature:atlas` | کاتالوگ، جزئیات و ثبت رصد، همراه با entry و resolver سرویس جرم آسمانی |
| `:feature:journal` | دفتر رصد، تب‌های محلی و باز کردن جزئیات جرم از طریق سرویس |
| `:app` | اتصال پیاده‌سازی داده و entryهای فیچرها، NavHost، Activity، میز ابزار و adapterهای Android |

در فیچر [Atlas](feature/atlas/src/main/kotlin/dev/nightatlas/feature/atlas)، اسکرین و ViewModel مربوط به هر قابلیت در `catalog`، `observation` یا `plan` کنار هم‌اند. در [Journal](feature/journal/src/main/kotlin/dev/nightatlas/feature/journal) همین الگو با `overview` و `observation` به کار رفته است. گراف‌ها و قراردادهای مقصد در `navigation` و ثبت‌های Hilt در `di` هستند.

در [app](app/src/main/kotlin/dev/nightatlas/app)، میز ابزار در `fieldkit`، فرم مسیر در `fieldkit.editor` و مثال‌های پشته در `fieldkit.trail` قرار دارند. `navigation` میزبان و اتصال به Android را نگه می‌دارد. نوع‌های وضعیت رابط که Android با نام کلاس ذخیره می‌کند، نام قبلی خود را برای بازیابی وضعیت حفظ کرده‌اند.

در مسیر یک کارت پیکربندی، `ActionDto.toDomainModel(registry)` یک `ActionEntity` می‌سازد. `NavigationContainer.openAction` آن را به `ActionRouterImpl` می‌دهد. برای سرویس، `NavigationRegistry` نام را پیدا می‌کند و resolver فیچر مقصد را می‌سازد. گراف همان فیچر مقصد را نمایش می‌دهد. در این مثال، `FieldKitScreen.kt` انتخاب کاربر را با `onExampleSelected` به `KitGraph.kt` می‌دهد. گراف اکشن تایپ‌شدهٔ کارت را از نمونه‌های ورودی در `KitCards.kt` انتخاب می‌کند. ثبت سرویس را در `AtlasEntry.kt` ببین و از `AtlasGraph.kt` به `ObjectDetailScreen.kt` برو.

هسته یک اقتباس محلی از سورس `core/navigation` در کامیت `7079132` پروژهٔ مرجع است. الگوریتم dispatch، اولویت aliasها، ثبت entryها و تحویل pending از همان پیاده‌سازی گرفته شده‌اند. scheme، تب‌ها، شناسه‌های مشترک و wiring با دامنهٔ اطلس تطبیق داده شده‌اند. هر ماژول entry خودش را با `@Binds` و `@IntoSet` در `SingletonComponent` ثبت می‌کند. Hilt مجموعهٔ `FeatureEntry`ها را به `NavigationRegistryFactory` تزریق می‌کند. با اضافه کردن وابستگی یک فیچر به اپ، entry آن هم در رجیستری قرار می‌گیرد. در نسخهٔ نمونه، باز کردن یک تب مشخص بر وضعیت قبلی آن اولویت دارد، جایگزینی مقصد آرگومان‌های آن را حفظ می‌کند و حذف تعدادی صفحه در ریشه متوقف می‌شود. پروژه برای build یا اجرا فقط به فایل‌های همین پوشه نیاز دارد.

## قرارداد مسیریابی

گراف‌ها از `GraphNavigator` استفاده می‌کنند. مقصدهای تایپ‌شده `NavigationRoute` را پیاده می‌کنند و با `openRoute` باز می‌شوند. مسیر رشته‌ای با `NamedRoute` به همان متد داده می‌شود. برای اکشن، پیوند، سرویس و عملیات پشته به‌ترتیب `openAction`، `openLink`، `openService` و `execute` وجود دارد. در `AtlasGraph`، هندلر انتخاب صورت فلکی ورودی `Constellation` می‌پذیرد و مقصد جزئیات را با شناسهٔ آن می‌سازد. `openRegistered` این ورودی را از رجیستری عبور می‌دهد. نام مسیر واردشده در فرم هنگام اجرا در گراف بررسی می‌شود.

در `NavigationContainer`، برای اکشن، URI و درخواست سرویس به‌ترتیب از `openAction(action)`، `openLink(uri)` و `openService(request)` استفاده کن. برای نمونه، درخواست جزئیات یک جرم آسمانی به شکل `openService(AtlasServices.Object(ObjectInput.Detail("lyra")))` نوشته می‌شود. خروجی هر resolver از نوع `NavigationTarget?` است و روتر همان مقدار را پردازش می‌کند. برای مثال سرویس می‌تواند یک `NamedRoute`، یک `NavigationTarget.Link` یا یک `NavigationTarget.Stack` برگرداند. رجیستری برای باز کردن مقصد، مسیر نام‌دار، مقصد تایپ‌شده یا مدل متصل به هندلر ثبت‌شده را می‌پذیرد. نتیجهٔ درخواست در کانتینر از نوع `NavigationResult` است.

| نتیجه | مثال |
| --- | --- |
| `Handled` | مقصد درخواستی باز شد یا عملیات پشته انجام شد. پیوند قدیمی که به مقصد خودش در گراف می‌رسد هم همین نتیجه را دارد. |
| `FallbackOpened` | شناسهٔ سرویس در یک اکشن شناخته نشد و صفحهٔ جایگزین واقعاً باز شد. این نتیجه از مسیرهای تو‌در‌توی سرویس و دیپ‌لینک هم به فراخواننده می‌رسد. |
| `Unhandled` | درخواست کامل نشد. نمونه‌ها شامل شناسهٔ مستقیم نامعتبر، وب‌سرویس بدون URL، یا صفحهٔ جایگزینی هستند که خودش باز نشد. |

`tryNavigate` همچنان برای دو نتیجهٔ اول `true` و برای نتیجهٔ سوم `false` می‌دهد. هر فیچر هنگام ثبت سرویس، تبدیل فیلدهای خام و ساخت مقصد از ورودی تایپ‌شده را تعریف می‌کند. دیپ‌لینک و JSON هنگام ورود از همین تبدیل استفاده می‌کنند. درخواست مستقیم از گراف، ورودی تایپ‌شده را مستقیماً به سرویس می‌دهد.

در رجیستری، اول alias دقیق یا وابسته به مسیر بررسی می‌شود. سپس از بین aliasهای نرمال‌شده‌ای که داخل کلید ورودی هستند، طولانی‌ترین تطبیق انتخاب می‌شود. تطبیق‌های هم‌طول فقط وقتی معتبرند که به یک سرویس برسند. در صورت تعارض، نتیجه نامشخص است و رجیستری `null` برمی‌گرداند. اگر هیچ aliasی تطبیق نداشت، شناسهٔ اصلی بررسی می‌شود.

`PopCount(n)` برای عدد مثبت صفحه‌ها را تا سقف درخواستی حذف می‌کند و ریشه را نگه می‌دارد. اگر فقط بخشی از درخواست انجام شود، صفحه‌های حذف‌شده به پشته برنمی‌گردند و نتیجه `Unhandled` است. `ReplaceCurrent` ورودی بالای پشته را با همان مقصد و آرگومان‌ها دوباره می‌سازد؛ موفقیت آن به وجود ورودی تازه بستگی دارد.

## تست

```sh
./gradlew testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

تست‌های واحد تبدیل پارامترهای ورودی و انتخاب قدم ثبت رصد را بررسی می‌کنند. تست‌های دستگاه ثبت سرویس‌ها و اولویت aliasها را در گراف واقعی Hilt می‌سنجند و مسیر ثبت تا دفتر، بازسازی Activity، Intent و دیپ‌لینک، سرویس ناشناخته و رفتار پشته را از UI اجرا می‌کنند.

package com.turing.codingsample.somesample.DummyServiceItem;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlProductGroupMap {

    private static final ImmutableMap<String, Integer> NAME_TO_ID_MAP = createIdToNameMap();

    public static Integer getIdByName(String name) {
        return NAME_TO_ID_MAP.get(name);
    }

    private static ImmutableMap<String, Integer> createIdToNameMap() {
        ImmutableMap<String, Integer> myMap = ImmutableMap.<String, Integer>builder()
                .put("gl_book", 14)
                .put("gl_music", 15)
                .put("gl_gift", 20)
                .put("gl_toy", 21)
                .put("gl_game", 22)
                .put("gl_electronics", 23)
                .put("gl_video", 27)
                .put("gl_batteries", 44)
                .put("gl_shops", 50)
                .put("gl_universal_catalog", 53)
                .put("gl_home_improvement", 60)
                .put("gl_video_games", 63)
                .put("gl_software", 65)
                .put("gl_dvd", 74)
                .put("gl_baby_product", 75)
                .put("gl_kitchen", 79)
                .put("gl_lawn_and_garden", 86)
                .put("gl_wireless", 107)
                .put("gl_ebooks", 111)
                .put("gl_photo", 114)
                .put("gl_drugstore", 121)
                .put("gl_slots", 123)
                .put("gl_catalog_of_the_world", 125)
                .put("gl_audible", 129)
                .put("gl_downloadable_software", 136)
                .put("gl_pc", 147)
                .put("gl_magazine", 153)
                .put("gl_target", 158)
                .put("gl_target_gift_card", 160)
                .put("gl_paper_catalog", 171)
                .put("gl_restaurant_menu", 180)
                .put("gl_apparel", 193)
                .put("gl_beauty", 194)
                .put("gl_food_and_beverage", 195)
                .put("gl_furniture", 196)
                .put("gl_jewelry", 197)
                .put("gl_luggage", 198)
                .put("gl_pet_products", 199)
                .put("gl_sports", 200)
                .put("gl_home", 201)
                .put("gl_cadillac", 219)
                .put("gl_media", 226)
                .put("gl_gift_card", 228)
                .put("gl_office_product", 229)
                .put("gl_travel_store", 234)
                .put("gl_sdp_misc", 236)
                .put("gl_watch", 241)
                .put("gl_loose_stones", 246)
                .put("gl_gourmet", 251)
                .put("gl_local_directories", 256)
                .put("gl_posters", 258)
                .put("gl_sports_memorabilia", 259)
                .put("gl_school_supplies", 260)
                .put("gl_art_craft_supplies", 261)
                .put("gl_medical_lab_supplies", 262)
                .put("gl_automotive", 263)
                .put("gl_art", 264)
                .put("gl_major_appliances", 265)
                .put("gl_antiques", 266)
                .put("gl_musical_instruments", 267)
                .put("gl_gift_certificates", 279)
                .put("gl_tires", 293)
                .put("gl_digital_short_lit", 297)
                .put("gl_digital_documents", 298)
                .put("gl_philanthropy", 304)
                .put("gl_authority_non_buyable", 307)
                .put("gl_shoes", 309)
                .put("gl_free_gift_card", 311)
                .put("gl_webservices", 313)
                .put("gl_library_services", 316)
                .put("gl_digital_video_download", 318)
                .put("gl_grocery", 325)
                .put("gl_digital_music_service", 327)
                .put("gl_biss", 328)
                .put("gl_digital_book_service", 334)
                .put("gl_electronic_gift_certificate", 336)
                .put("gl_digital_music_purchase", 340)
                .put("gl_digital_text", 349)
                .put("gl_digital_periodicals", 350)
                .put("gl_digital_ebook_purchase", 351)
                .put("gl_wireless_accessory", 353)
                .put("gl_wireless_prepaid_phone", 354)
                .put("gl_wireless_service_plan", 355)
                .put("gl_data_activity_plans", 356)
                .put("gl_advertising", 360)
                .put("gl_video_game_hardware", 362)
                .put("gl_personal_care_appliances", 364)
                .put("gl_digital_software", 366)
                .put("gl_digital_video_games", 367)
                .put("gl_wine", 370)
                .put("gl_membership", 392)
                .put("gl_prescription_drugs", 394)
                .put("gl_prescription_eyewear", 395)
                .put("gl_consumables_physical_gift_cards", 396)
                .put("gl_consumables_email_gift_cards", 397)
                .put("gl_digital_accessories", 400)
                .put("gl_video_membership", 402)
                .put("gl_mobile_apps", 405)
                .put("gl_digital_content_subscription", 406)
                .put("gl_digital_services", 407)
                .put("gl_digital_media_access_license", 408)
                .put("gl_downloadable_digital_media", 409)
                .put("gl_virtual_currency", 410)
                .put("gl_virtual_goods", 411)
                .put("gl_cloud_software_applications", 412)
                .put("gl_a_drive", 414)
                .put("gl_deal_sourcer", 416)
                .put("gl_amazon_sourced", 417)
                .put("gl_financial_products", 420)
                .put("gl_camera", 421)
                .put("gl_mobile_electronics", 422)
                .put("gl_digital_text_2", 424)
                .put("gl_digital_accessories_2", 425)
                .put("gl_publisher_services", 426)
                .put("gl_amazon_points", 437)
                .put("gl_digital_music_locker", 438)
                .put("gl_protected_collection", 439)
                .put("gl_entertainment_collectibles", 441)
                .put("gl_coins_collectibles", 442)
                .put("gl_stamps_collectibles", 443)
                .put("gl_arts_collectibles", 444)
                .put("gl_nonactivated_gift_cards", 446)
                .put("gl_social_games", 447)
                .put("gl_digital_products_1", 448)
                .put("gl_digital_products_2", 449)
                .put("gl_payment_devices", 450)
                .put("gl_digital_products_3", 451)
                .put("gl_local_business", 460)
                .put("gl_prime", 462)
                .put("gl_digital_devices_4", 465)
                .put("gl_pantry", 467)
                .put("gl_outdoors", 468)
                .put("gl_tools", 469)
                .put("gl_digital_accessories_4", 470)
                .put("gl_spark_books", 473)
                .put("gl_video_rental_by_mail", 475)
                .put("gl_3d_designs_and_print_on_demand", 484)
                .put("gl_digital_products_3_accessory", 485)
                .put("gl_digital_products_6", 487)
                .put("gl_digital_products_6_accessory", 489)
                .put("gl_digital_products_5", 493)
                .put("gl_digital_products_5_accessory", 494)
                .put("gl_3d_designs_and_manufacturing_services", 496)
                .put("gl_vehicle", 497)
                .put("gl_kindle_unlimited", 500)
                .put("gl_travel_and_vacation", 503)
                .put("gl_home_entertainment", 504)
                .put("gl_luxury_beauty", 510)
                .put("gl_designer_fashion", 515)
                .put("gl_guild", 517)
                .put("gl_fresh_perishable", 540)
                .put("gl_fresh_produce", 541)
                .put("gl_fresh_prepared", 542)
                .put("gl_fresh_ambient", 543)
                .put("gl_digital_products_7_accessory", 545)
                .put("gl_digital_products_8_accessory", 546)
                .put("gl_digital_products_9_accessory", 547)
                .put("gl_digital_products_10_accessory", 548)
                .put("gl_digital_products_11_accessory", 549)
                .put("gl_digital_products_12_accessory", 550)
                .put("gl_digital_products_13_accessory", 551)
                .put("gl_digital_products_14_accessory", 552)
                .put("gl_digital_products_15_accessory", 553)
                .put("gl_digital_products_16_accessory", 554)
                .put("gl_digital_products_17_accessory", 555)
                .put("gl_digital_products_18_accessory", 556)
                .put("gl_digital_products_19_accessory", 557)
                .put("gl_digital_products_20_accessory", 558)
                .put("gl_digital_products_21_accessory", 559)
                .put("gl_digital_products_22_accessory", 560)
                .put("gl_digital_products_23_accessory", 561)
                .put("gl_digital_products_24_accessory", 562)
                .put("gl_digital_products_25_accessory", 563)
                .put("gl_digital_products_26_accessory", 564)
                .put("gl_digital_products_7", 570)
                .put("gl_westlake", 582)
                .put("gl_real_estate", 590)
                .put("gl_services", 591)
                .put("gl_live_pets", 592)
                .put("gl_value_added_services", 594)
                .put("gl_digital_products_8", 596)
                .put("gl_subscribe_with_amazon", 597)
                .put("gl_liquidation_marketplace", 598)
                .put("gl_comixology_unlimited", 609)
                .put("gl_amazon_money", 610)
                .put("gl_imdb_pro", 611)
                .put("gl_digital_video_subscription", 613)
                .put("gl_entertainment_tickets", 616)
                .put("gl_fsn_devices", 618)
                .put("gl_twitch", 620)
                .put("gl_prime_video_subscription", 624)
                .put("gl_music_premium_services", 626)
                .put("gl_amazon_video_subscription", 628)
                .put("gl_author_services", 632)
                .put("gl_aws_training", 633)
                .put("gl_twitch_fuel", 634)
                .put("gl_vdo_devices", 637)
                .put("gl_lumberyard_marketplace", 639)
                .put("gl_twitch_prime", 640)
                .put("gl_softlines_private_label", 641)
                .put("gl_bill_payments", 644)
                .put("gl_sud_devices", 645)
                .put("gl_alexa", 649)
                .put("gl_amazon_rapids", 650)
                .put("gl_zephyr", 653)
                .put("gl_vicc_subscriptions", 655)
                .put("gl_amazon_prints", 657)
                .put("gl_project_diamond", 663)
                .put("gl_sud_accessories", 667)
                .put("gl_palomar", 670)
                .put("gl_aiq", 673)
                .put("gl_twitch_physical", 675)
                .put("gl_primelive_tickets", 679)
                .put("gl_sarek", 682)
                .put("gl_aws_devices", 685)
                .put("gl_mas", 686)
                .put("gl_moments", 689)
                .put("gl_amazon_meal_kits", 693)
                .put("gl_amazon_prepared_foods", 694)
                .put("gl_amazon_studios_kids", 697)
                .put("gl_digisubs_education", 698)
                .put("gl_digital_products_9", 709)
                .put("gl_digital_products_9_services", 710)
                .put("gl_music_ad_supported_services", 711)
                .put("gl_aws_new_products", 712)
                .put("gl_short_form_stories", 714)
                .put("gl_print_magazine_subscription", 715)
                .put("gl_crosscountry_gift_card", 716)
                .put("gl_bond_luxury_beauty", 721)
                .put("gl_amazon_pay", 725)
                .put("gl_short_form_stories_token", 726)
                .put("gl_digital_test_prep", 732)
                .put("gl_digital_products_10", 734)
                .put("gl_pae_promotions", 735)
                .put("gl_insurance", 742)
                .put("gl_digital_video_subscription_bounties", 744)
                .put("gl_amazon_video_subscription_bounties", 745)
                .put("gl_digital_video_download_bounties", 746)
                .put("gl_imdbtv_partner_royalties", 747)
                .put("gl_uv_services", 748)
                .put("gl_uv_products", 749)
                .put("gl_jadzia", 750)
                .put("gl_protein", 751)
                .put("gl_non_inventory_supplies", 752)
                .put("gl_news", 753)
                .put("gl_creator_connections", 754)
                .put("gl_alexa_voice_services", 755)
                .put("gl_spoken_word", 756)
                .put("gl_alexa_auto", 757)
                .put("gl_care_plus", 758)
                .build();
        return myMap;
    }
}
